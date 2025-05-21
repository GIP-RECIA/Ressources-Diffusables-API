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
import fr.recia.ressourcesdiffusablesapi.service.cache.exceptions.CacheUpdateFailureException;
import fr.recia.ressourcesdiffusablesapi.service.cache.io.ICacheFileIO;
import fr.recia.ressourcesdiffusablesapi.service.cache.io.impl.CacheFileIO;
import fr.recia.ressourcesdiffusablesapi.service.dao.IRessourceDiffusableDAO;
import fr.recia.ressourcesdiffusablesapi.service.dao.exceptions.RessourceDiffusableDAOException;
import fr.recia.ressourcesdiffusablesapi.service.dao.impl.RessourceDiffusableDAOLocalJsonImpl;
import fr.recia.ressourcesdiffusablesapi.service.parser.IRessourceDiffusableParserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CacheServiceJsonImpl implements ICacheService {

    private Clock clock;

    private final AppProperties appProperties;

    private final IRessourceDiffusableDAO ressourceDiffusableDAO;

    private final IRessourceDiffusableParserService ressourceDiffusableParserService;

    private final ICacheFileIO cacheFileIO;

    private LocalDateTime downloadLDT = LocalDateTime.MIN;

    private LocalDateTime expiryLDT = LocalDateTime.MIN;

    private List<RessourceDiffusable> ressourceDiffusableList;


    public CacheServiceJsonImpl(AppProperties appProperties, IRessourceDiffusableDAO ressourceDiffusableDAO, IRessourceDiffusableParserService ressourceDiffusableParserService, Clock clock, ICacheFileIO cacheFileIO) {
        this.clock = clock;
        this.cacheFileIO = cacheFileIO;
        this.appProperties = appProperties;
        this.ressourceDiffusableDAO = ressourceDiffusableDAO;
        this.ressourceDiffusableParserService = ressourceDiffusableParserService;
    }

    @Override
    public List<RessourceDiffusable> getRessourceDiffusableList() {

        LocalDateTime localDateTime = LocalDateTime.now(clock);
        if(LocalDateTime.now(clock).isAfter(expiryLDT)){
            log.info("Cache expired");
            try {
                updateRessourceDiffusableList();
                this.expiryLDT = localDateTime.plusSeconds(appProperties.getCache().getCacheLifetimeInSeconds());
                log.info("Cache refresh successful, will expire the {}", this.expiryLDT);
            } catch (CacheUpdateFailureException e) {
               if(Objects.nonNull(this.ressourceDiffusableList)){
                   this.expiryLDT = LocalDateTime.now(this.clock).plusMinutes(30);
                   log.warn("Due to cache update fail, set current cache expiry to {}, will use previously known ressources diffusables before next refresh", this.expiryLDT);
                   return this.ressourceDiffusableList;
               }else {
                   throw new RuntimeException("No ressources diffusables available");
               }
            }
        }

        return ressourceDiffusableList;
    }

    private void updateRessourceDiffusableList() throws CacheUpdateFailureException {
        log.info("Update of ressource diffusable list started");
        String rawValue = null;
        try {
            log.debug("Invoking DAO");
            rawValue = this.ressourceDiffusableDAO.getRessourceDiffusableRawJsonString();
        } catch (RessourceDiffusableDAOException e) {
            log.error("Exception when invoking DAO: ",e);
        }

        if(Objects.nonNull(rawValue) && Strings.isNotEmpty(rawValue)){
            try {
                log.info("Writing DAO response to cache file");
                cacheFileIO.writeRawJsonToCacheFile(rawValue);
            } catch (IOException e) {
                log.error("Exception when writing DAO response to cache file: ",e);
            }
        }else {
            try {
                log.info("Reading DAO response from cache file");
                rawValue = cacheFileIO.getRawJsonFromCacheFile();
                this.expiryLDT = LocalDateTime.now(this.clock).plusMinutes(30);
                log.warn("Due to using already cached data, set cache expiry to {}, will use previously known ressources diffusables before next refresh", this.expiryLDT);
            } catch (IOException e) {
                log.error("Exception when reading previous cache file:", e);
                // here there is neither response from DAO neither data from a previous cache we can't update the list
                throw new CacheUpdateFailureException("Failed");
            }
        }
        try {
            log.info("Parsing DAO response");
            this.ressourceDiffusableList = ressourceDiffusableParserService.parseRawJsonStringIntoRessourceDiffusableList(rawValue);
        } catch (IOException e) {
            log.error("Exception when parsing raw json string:", e);
            throw new CacheUpdateFailureException("Failed");
        }
    }


}
