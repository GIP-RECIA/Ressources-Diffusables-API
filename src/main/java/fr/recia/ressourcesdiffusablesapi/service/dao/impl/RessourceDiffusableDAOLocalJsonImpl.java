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
package fr.recia.ressourcesdiffusablesapi.service.dao.impl;

import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.service.dao.RessourceDiffusableDAOAbstractImpl;
import fr.recia.ressourcesdiffusablesapi.service.dao.exceptions.NoGarJsonFileNotFound;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Slf4j
@Resource
public class RessourceDiffusableDAOLocalJsonImpl extends RessourceDiffusableDAOAbstractImpl {


    private final AppProperties appProperties;

    public RessourceDiffusableDAOLocalJsonImpl(AppProperties appProperties){
        this.appProperties = appProperties;
        log.info("Created local json DAO");

    }

    @Override
    public void refreshRessourceDiffusableFile() {
        File file = new File(getFileLocalURI());
        if(!file.exists()){
            log.error("throwing");
            throw new NoGarJsonFileNotFound(String.format("Could not found local json file at location %s", getFileLocalURI()));
        }
        log.debug("FOUND JSON FILE NO-GAR");
    }

    @Override
    protected URI getFileLocalURI() {
        try {
            log.warn("{}",appProperties.getNoGar());
            log.warn("{}",appProperties.getNoGar().getLocalJsonFilePath());
            log.warn("{}",RessourceDiffusableDAOLocalJsonImpl.class.getResource(appProperties.getNoGar().getLocalJsonFilePath()));

            URI localFileURI = Objects.requireNonNull(RessourceDiffusableDAOLocalJsonImpl.class.getResource(appProperties.getNoGar().getLocalJsonFilePath())).toURI();
            log.debug("file local URI is: {}", localFileURI);
            return localFileURI;
        } catch (URISyntaxException | NullPointerException e) {
            log.error("exception :", e);
            throw new NoGarJsonFileNotFound(String.format("Could not found local json file at location %s", appProperties.getNoGar().getLocalJsonFilePath()));
        }
    }


}
