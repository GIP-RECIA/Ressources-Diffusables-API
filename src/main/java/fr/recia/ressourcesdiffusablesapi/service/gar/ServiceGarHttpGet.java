/*
 * Copyright (C) 2021 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.ressourcesdiffusablesapi.service.gar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.config.beans.GARProperties;
import fr.recia.ressourcesdiffusablesapi.model.AttributRessource;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.model.jsonmirror.RessourcesDiffusablesWrappingJsonMirror;
import fr.recia.ressourcesdiffusablesapi.service.cache.ServiceCacheHistorique;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public class ServiceGarHttpGet implements ServiceGar {

    private final GARProperties garProperties;

    @Autowired
    private ServiceCacheHistorique serviceCacheHistorique;

    ServiceCacheHistorique getServiceCacheHistorique(){
        return  serviceCacheHistorique;
    }

    private List<RessourceDiffusable> ressourcesDiffusablesComplet = new ArrayList<>();

    private File ressourcesDiffusablesFile = null;

    File getRessourcesDiffusablesFile(){
        return ressourcesDiffusablesFile;
    }

    private LocalDateTime dateGeneration = null;

    private LocalDateTime dateTelechargement = null;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ServiceGarHttpGet(AppProperties appProperties) {
        this.garProperties = appProperties.getGar();
    }

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

    private List<RessourceDiffusable> rechercher(RessourceDiffusableFilter filter) {
        // On vérifie que les données sont toujours valides.
        this.verifValidite();

        if (filter.isEmpty()) { // Soit le filtre est vide...
            if (log.isDebugEnabled()) log.debug("Ressources diffusables request: No filter; no need to check history");
            return this.ressourcesDiffusablesComplet;
        } else { // Soit il faut faire un filtrage. Il est peut-être historisé.
            List<RessourceDiffusable> ressourcesDiffusablesHistorisees = this.serviceCacheHistorique.get(filter);
            if (ressourcesDiffusablesHistorisees != null) {
                if (log.isDebugEnabled())
                    log.debug("Ressources diffusables request: Getting request result from history");
                return ressourcesDiffusablesHistorisees;
            } else {
                List<RessourceDiffusable> ressourcesDiffusablesFiltrees = new ArrayList<>();
                for (RessourceDiffusable ressourceDiffusable : this.ressourcesDiffusablesComplet) {
                    if (filter.filter(ressourceDiffusable)) ressourcesDiffusablesFiltrees.add(ressourceDiffusable);
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

    void verifValidite() {
        if (this.dateTelechargement == null || SECONDS.between(this.dateTelechargement, LocalDateTime.now()) > garProperties.getCacheDuration())
//            this.ressourcesDiffusablesFile = new File(garProperties.getDownloadLocationPath());
//            lireFichier();
//            if(false)
              this.ressourcesDiffusablesComplet = getRessourceDiffusablesFromRessourcesDiffusablesUri();
    }

    protected List<RessourceDiffusable> getRessourceDiffusablesFromRessourcesDiffusablesUri(){
        boolean downloaded = telechargerFichier();
        File currentFile = new File(garProperties.getDownloadLocationPath());
        if (downloaded){
            return this.lireFichier();
        } else if(currentFile.exists() ){
            return this.lireFichier();
        } else {
            return new ArrayList<>(); // ou retourner une exception
        }
    }

    private boolean telechargerFichier() {
        String downloadFileLocationFull = garProperties.getDownloadLocationPath();
        String tempDownloadFileLocationFull = downloadFileLocationFull+ ".temp";
        File jsonFile = new File(downloadFileLocationFull);
        File tempJsonFile = new File(tempDownloadFileLocationFull);

        boolean downloadFolderFileExist = jsonFile.getParentFile().exists();
        if(!downloadFolderFileExist){
            boolean createdDownloadFolderFile = jsonFile.getParentFile().mkdirs();
            if(createdDownloadFolderFile)
                log.info("Created folder "+jsonFile.getParentFile().getPath() + " ");
            //add else in upcoming refactor
        }

        // Début du téléchargement.
        if (log.isInfoEnabled())
            log.info("Ressources diffusables source file download: Starting download procedure");

        // Identification du fichier.
        if (this.getRessourcesDiffusablesFile() == null)
            this.ressourcesDiffusablesFile = jsonFile;


        // Téléchargement du fichier.
        try {
            String contextURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

            if(contextURL.contains("://")){
                contextURL = contextURL.split("://")[1];
            }
            String domainURL = contextURL.split("/")[0];
            String prefix = "https://";
            String combinedDomainURL = prefix + domainURL;
            URL baseURL = new URL(combinedDomainURL);
            URL mergedURL = new URL(baseURL, garProperties.getRessourcesDiffusablesUri());
            log.info("Downloading JSON file from : "+mergedURL);
            log.info("Downloading JSON file to : "+tempJsonFile.getPath());
            new FileOutputStream(tempJsonFile.getPath())
                    .getChannel()
                    .transferFrom(
                            Channels.newChannel(mergedURL.openStream()),
                            0,
                            Long.MAX_VALUE
                    );
        }  catch (MalformedURLException malformedURLException) {
            log.error("Ressources diffusables source file download: malformed URL exception, ", malformedURLException);
            return false;

        } catch (IOException e) {
            log.error("Couldn't download JSON file, "+ e.getMessage());
            return false;
            }

        // Mise à jour de la date.
        this.dateTelechargement = LocalDateTime.now();

        // Suppression de l'historique.
        this.serviceCacheHistorique.clear();

        // Fin de téléchargement
        if (log.isInfoEnabled())
            log.info("Ressources diffusables source file download: ressources diffusables source file successfully downloaded!");

        try {
            Files.move(Paths.get(tempJsonFile.getPath()), Paths.get(jsonFile.getPath()), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;

//        catch (IOException ioException) {
//            log.error("Ressources diffusables source file download: IO exception", ioException);
//        }
    }

    List<RessourceDiffusable> lireFichier() {
        if (log.isDebugEnabled()) {
            log.debug("Reading of ressources diffusables source file: Starting reading procedure");
            log.debug("Reading of ressources diffusables source file: file is at {}", this.getRessourcesDiffusablesFile().getAbsolutePath());
        }

        File jsonFIle = getRessourcesDiffusablesFile();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
            RessourcesDiffusablesWrappingJsonMirror[] result = mapper.readValue(jsonFIle, RessourcesDiffusablesWrappingJsonMirror[].class);

            if(result.length < 1){
                throw new ArrayIndexOutOfBoundsException();
            }
            if (log.isDebugEnabled())
                log.debug("Reading of ressources diffusables source file: Ressources diffusables source file successfully read!");
            return result[0].getRessourcesDiffusables();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
