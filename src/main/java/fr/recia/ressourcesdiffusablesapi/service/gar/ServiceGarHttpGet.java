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
import fr.recia.ressourcesdiffusablesapi.service.cache.ServiceCache;
import fr.recia.ressourcesdiffusablesapi.service.cache.ServiceCacheJson;
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
    private final ServiceCache serviceCache;

    private List<RessourceDiffusable> ressourcesDiffusablesComplet = new ArrayList<>();

    private File ressourcesDiffusablesFile = null;

    File getRessourcesDiffusablesFile(){
        return ressourcesDiffusablesFile;
    }

    private LocalDateTime dateGeneration = null;

    private LocalDateTime dateTelechargement = null;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ServiceGarHttpGet(AppProperties appProperties, ServiceCache serviceCache) {
        this.garProperties = appProperties.getGar();
        this.serviceCache = serviceCache;
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
        } else {
            List<RessourceDiffusable> ressourcesDiffusablesFiltrees = new ArrayList<>();
            for (RessourceDiffusable ressourceDiffusable : this.ressourcesDiffusablesComplet) {
                if (filter.filter(ressourceDiffusable)) ressourcesDiffusablesFiltrees.add(ressourceDiffusable);
            }
            return ressourcesDiffusablesFiltrees;
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
              this.ressourcesDiffusablesComplet = getRessourceDiffusablesFromRessourcesDiffusablesUri();
    }

    protected List<RessourceDiffusable> getRessourceDiffusablesFromRessourcesDiffusablesUri(){
        return serviceCache.getAllRessourcesDiffusables();
    }


}
