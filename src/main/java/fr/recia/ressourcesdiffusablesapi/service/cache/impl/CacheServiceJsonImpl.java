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
package fr.recia.ressourcesdiffusablesapi.service.cache.impl;

import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.service.cache.ICacheService;
import fr.recia.ressourcesdiffusablesapi.service.dao.IRessourceDiffusableDAO;
import fr.recia.ressourcesdiffusablesapi.service.parser.IRessourceDiffusableParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CacheServiceJsonImpl implements ICacheService {

    private Clock clock;

    private final AppProperties appProperties;

    private final IRessourceDiffusableDAO ressourceDiffusableDAO;

    private final IRessourceDiffusableParserService ressourceDiffusableParserService;

    private LocalDateTime downloadLDT = LocalDateTime.MIN;

    private LocalDateTime expiryLDT = LocalDateTime.MIN;

    private List<RessourceDiffusable> ressourceDiffusableList;

    public CacheServiceJsonImpl(AppProperties appProperties, IRessourceDiffusableDAO ressourceDiffusableDAO, IRessourceDiffusableParserService ressourceDiffusableParserService, Clock clock) {
        this.clock = clock;
        this.appProperties = appProperties;
        this.ressourceDiffusableDAO = ressourceDiffusableDAO;
        this.ressourceDiffusableParserService = ressourceDiffusableParserService;
    }

    @Override
    public List<RessourceDiffusable> getRessourceDiffusableList() {

        LocalDateTime localDateTime = LocalDateTime.now(clock);
        if(LocalDateTime.now(clock).isAfter(expiryLDT)){
            updateRessourceDiffusableList();
        }

        return ressourceDiffusableList;
    }

    private void updateRessourceDiffusableList() {
        // call to DAO
        try{
            this.ressourceDiffusableDAO.refreshRessourceDiffusableFile();
            this.downloadLDT = LocalDateTime.now(clock);
            this.expiryLDT = downloadLDT.plusSeconds(appProperties.getCacheLifetimeInSeconds());
            this.ressourceDiffusableList = this.ressourceDiffusableParserService.parseJsonIntoRessourceDiffusableList(this.ressourceDiffusableDAO.getLocalFile());
            return;
        } catch (IOException ioException) {
            // if whe still have a JSON from previous time ( GAR ) we read it
            // doesn't need to check if GAR or no-GAR, no-GAR throw exception instead of returning false
            File localFileFromDAO = null;
            try {
                localFileFromDAO = this.ressourceDiffusableDAO.getLocalFile();
                this.ressourceDiffusableList = this.ressourceDiffusableParserService.parseJsonIntoRessourceDiffusableList(this.ressourceDiffusableDAO.getLocalFile());
            } catch (FileNotFoundException fileNotFoundException) {
                log.warn(fileNotFoundException.getMessage());
                // if we have neither a json nor values in bean we throw an exception

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to retrieve any RessourceDiffusable, neither from Mediacentre-WS nor local JSON file");
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("First error: ");
                stringBuilder.append(ioException.getMessage());

                if(Objects.isNull(ressourceDiffusableList) && Objects.isNull(localFileFromDAO) ){
                    throw new RuntimeException("unable to retrieve resources, can't get");
                }
            }

        }


    }
}
