package fr.recia.ressourcesdiffusablesapi.service.gar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.ressourcesdiffusablesapi.model.AttributRessource;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.service.cache.ServiceCacheHistorique;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public class ServiceGarHttpGet implements ServiceGar {
    // Attributs
    @Autowired
    private ServiceCacheHistorique serviceCacheHistorique;

    @Value("${service-gar-http-get.ressources-diffusables-uri:}")
    private String ressourcesDiffusablesUri;

    @Value("${service-gar-http-get.download-location-path}")
    private String downloadLocationPath;

    private final List<RessourceDiffusable> ressourcesDiffusablesComplet = new ArrayList<>();

    private File ressourcesDiffusablesFile = null;

    private LocalDateTime dateGeneration = null;

    private LocalDateTime dateTelechargement = null;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Getteurs
    @Override
    public int getSize(RessourceDiffusableFilter filter) {
        return this.rechercher(filter).size();
    }

    @Override
    public int getPageCount(int elementsParPage, RessourceDiffusableFilter filter) {
        return (int) Math.ceil(this.rechercher(filter).size() / (double) elementsParPage);
    }

    @Override
    public Collection<RessourceDiffusable> getRessourcesDiffusables(int page, int ressourcesPerPage, RessourceDiffusableFilter filter) {
        return this.genererPage(this.rechercher(filter), page, ressourcesPerPage);
    }

    // M??thodes
    private List<RessourceDiffusable> rechercher(RessourceDiffusableFilter filter) {
        // On v??rifie que les donn??es sont toujours valides.
        this.verifValidite();

        if (filter.isEmpty()) { // Soit le filtre est vide...
            if(log.isDebugEnabled()) log.debug("Ressources diffusables request: No filter; no need to check history");
            return this.ressourcesDiffusablesComplet;
        } else { // Soit il faut faire un filtrage. Il est peut-??tre historis??.
            List<RessourceDiffusable> ressourcesDiffusablesHistorisees = this.serviceCacheHistorique.get(filter);
            if (ressourcesDiffusablesHistorisees != null) {
                if(log.isDebugEnabled()) log.debug("Ressources diffusables request: Getting request result from history");
                return ressourcesDiffusablesHistorisees;
            } else {
                List<RessourceDiffusable> ressourcesDiffusablesFiltrees = new ArrayList<>();
                for (RessourceDiffusable ressourceDiffusable : this.ressourcesDiffusablesComplet) {
                    if (filter.filter(ressourceDiffusable)) {
                        ressourcesDiffusablesFiltrees.add(ressourceDiffusable);
                    }
                }
                this.serviceCacheHistorique.put(filter, ressourcesDiffusablesFiltrees);
                return ressourcesDiffusablesFiltrees;
            }
        }
    }

    private List<RessourceDiffusable> genererPage(List<RessourceDiffusable> ressourcesDiffusablesTotal, int page, int elementsParPage) {
        List<RessourceDiffusable> ressourcesDiffusables = new ArrayList<>();
        for (int i = page * elementsParPage; i < Math.min((page + 1) * elementsParPage, ressourcesDiffusablesTotal.size()); i++) {
            ressourcesDiffusables.add(ressourcesDiffusablesTotal.get(i));
        }
        return ressourcesDiffusables;
    }

    private void ajouterRessource(RessourceDiffusable ressourceDiffusable) {
        this.ressourcesDiffusablesComplet.add(ressourceDiffusable);
    }

    private void verifValidite() {
        if (this.dateTelechargement == null || SECONDS.between(this.dateTelechargement, LocalDateTime.now()) > 86400) { // 24h.
            this.telechargerFichier();
        }
    }

    private void telechargerFichier() {
        try {
            // D??but du t??l??chargement.
            if(log.isInfoEnabled()) log.info("Ressources diffusables source file download: Starting download procedure");
            if(log.isDebugEnabled()) log.debug("Ressources diffusables source file download: URL is {}", this.ressourcesDiffusablesUri);

            // Identification du fichier.
            if (this.ressourcesDiffusablesFile == null) {
                this.ressourcesDiffusablesFile = new File(this.downloadLocationPath);
            }

            // Cr??ation du r??pertoire parent s'il n'existe pas.
            if (!this.ressourcesDiffusablesFile.getParentFile().exists()){
                this.ressourcesDiffusablesFile.getParentFile().mkdirs();
            }

            // T??l??chargement du fichier.
            new FileOutputStream(this.downloadLocationPath)
                    .getChannel()
                    .transferFrom(
                            Channels.newChannel(new URL(this.ressourcesDiffusablesUri).openStream()),
                            0,
                            Long.MAX_VALUE
                    );

            // Mise ?? jour de la date.
            this.dateTelechargement = LocalDateTime.now();

            // Suppression de l'historique.
            this.serviceCacheHistorique.clear();

            // Fin de t??l??chargement
            if(log.isInfoEnabled()) log.info("Ressources diffusables source file download: ressources diffusables source file successfully downloaded!");

            // Lancement de la lecture du fichier.
            this.lireFichier();

        } catch (MalformedURLException malformedURLException) {
            log.error("Ressources diffusables source file download: malformed URL exception", malformedURLException);
        } catch (IOException ioException) {
            log.error("Ressources diffusables source file download: IO exception", ioException);
        }
    }

    private void lireFichier() {
        if(log.isDebugEnabled()) log.debug("Reading of ressources diffusables source file: Starting reading procedure");
        if(log.isDebugEnabled()) log.debug("Reading of ressources diffusables source file: file is at {}", this.ressourcesDiffusablesFile.getAbsolutePath());

        try {
            // Lecture du JSON.
            JsonNode jsonNode = objectMapper.readTree(this.ressourcesDiffusablesFile);

            // Ouverture du singleton.
            jsonNode = jsonNode.get(0);

            // R??cup??ration de la date de g??n??ration.
            LocalDateTime dateGeneration = LocalDateTime.parse(
                    jsonNode.get("dateGeneration").asText().replaceAll(" ", ""),
                    DateTimeFormatter.ISO_DATE_TIME
            );
            if(log.isDebugEnabled()) log.debug(
                    "Reading of ressources diffusables source file: Ressources diffusables source file generation date: {}",
                    DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss").format(dateGeneration)
            );

            // Est-ce que cela est utile de le relire ?
            if (this.dateGeneration != null && dateGeneration.isEqual(this.dateGeneration)) {
                if(log.isDebugEnabled()) log.debug("Reading of ressources diffusables source file: The file that has been previously read and the file currently read are the same; stopping the reading procedure");
                return;
            } else {
                this.dateGeneration = dateGeneration;
            }

            // Ouverture de l'objet des ressources diffusables.
            jsonNode = jsonNode.get("ressourceDiffusable");

            // Cr??ation de la map qui sert au cas o?? des ID viennent sans les noms.
            Map<String, String> attributsPseudoCache = new HashMap<>();

            // Pour chaque ressource diffusable.
            for (int i = 0; jsonNode.has(i); i++) {
                // R??cup??ration du node JSON.
                JsonNode ressourceDiffusableJson = jsonNode.get(i);

                // V??rification que ce n'est pas une "m??re de famille".
                // Selon le standard, une ressource "m??re de famille" est une ressource virtuelle qui est connect??e
                // ?? d'autres ressources dites "membres de famille". Au final ce n'est pas une vraie ressource.
                if (ressourceDiffusableJson.has("mereFamille")) {
                    continue;
                }

                // Nom et ID de la ressource.
                String idRessource = ressourceDiffusableJson.get("idRessource").asText();
                JsonNode nomRessourceJson = ressourceDiffusableJson.get("nomRessource");
                if (nomRessourceJson != null) {
                    attributsPseudoCache.put(idRessource, nomRessourceJson.asText());
                }

                // Nom et ID de l'??diteur.
                String idEditeur = ressourceDiffusableJson.get("idEditeur").asText();
                JsonNode nomEditeurJson = ressourceDiffusableJson.get("nomEditeur");
                if (nomEditeurJson != null) {
                    attributsPseudoCache.put(idEditeur, nomEditeurJson.asText());
                }

                // R??cup??ration du node JSON des distributeurs com.
                JsonNode distributeursComJson = ressourceDiffusableJson.get("distributeursCom");

                // Cr??ation de la liste des distributeurs com.
                List<String> listeIdDistributeurCom = new ArrayList<>();

                // It??ration sur tous les distributeurs com.
                for (int j = 0; distributeursComJson.has(j); j++) {
                    // Nom et ID du distributeur com.
                    String idDistributeurCom = distributeursComJson.get(j).get("distributeurCom").asText();
                    JsonNode nomDistributeurComJson = distributeursComJson.get(j).get("nomDistributeurCom");
                    if (nomDistributeurComJson != null) {
                        attributsPseudoCache.put(idDistributeurCom, nomDistributeurComJson.asText());
                    }

                    // On ajoute le distributeur com ?? la liste des distributeurs com ?? ajouter.
                    listeIdDistributeurCom.add(idDistributeurCom);
                }

                // Nom et ID de l'??diteur.
                String idDistributeurTech = ressourceDiffusableJson.get("distributeurTech").asText();
                JsonNode nomDistributeurTech = ressourceDiffusableJson.get("nomDistributeurTech");
                if (nomDistributeurTech != null) {
                    attributsPseudoCache.put(idDistributeurTech, nomDistributeurTech.asText());
                }

                // Est-ce que la ressource est affichable ?
                boolean affichable = ressourceDiffusableJson.get("affichable").asBoolean();

                // Est-ce que la ressource est diffusable ?
                boolean diffusable = ressourceDiffusableJson.get("diffusable").asBoolean();

                // On cr??e les objets AttributRessources ?? ce moment l?? seulement car si le nom d'une des ressources est
                // indiqu??e dans la ressource suivante il faut d'abord lire cette suivante avant de g??n??rer la premi??re.
                // Exemple :
                //     editeur: {"130006042_0000000106379136", "");
                //     distributeurTech: {"130006042_0000000106379136", "Agrosup Dijon"}
                // Il y a ce genre d'ambigu??t?? dans le fichier t??l??charg??.
                this.ajouterRessource(
                        new RessourceDiffusable(
                                new AttributRessource(
                                        idRessource,
                                        attributsPseudoCache.getOrDefault(idRessource, "")
                                ),
                                new AttributRessource(
                                        idEditeur,
                                        attributsPseudoCache.getOrDefault(idEditeur, "")
                                ),
                                listeIdDistributeurCom.stream().map(
                                        idDistributeurCom -> new AttributRessource(
                                                idDistributeurCom,
                                                attributsPseudoCache.getOrDefault(idDistributeurCom, "")
                                        )
                                ).collect(Collectors.toList()),
                                new AttributRessource(
                                        idDistributeurTech,
                                        attributsPseudoCache.getOrDefault(idDistributeurTech, "")
                                ),
                                affichable,
                                diffusable
                        )
                );
            }
        } catch (JsonProcessingException e) {
            log.error("Reading of ressources diffusables source file: JSON processing exception");
            e.printStackTrace();
        } catch (IOException ioException) {
            log.error("Reading of ressources diffusables source file: IO exception");
            ioException.printStackTrace();
        }

        // Suppression de l'historique.
        this.serviceCacheHistorique.clear();

        // Fin de lecture.
        if(log.isDebugEnabled()) log.debug("Reading of ressources diffusables source file: Ressources diffusables source file successfully read!");
    }
}
