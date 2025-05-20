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
import fr.recia.ressourcesdiffusablesapi.service.dao.exceptions.RessourceDiffusableDAOException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public String getRessourceDiffusableRawJsonString() throws RessourceDiffusableDAOException {
        try {
          URI uri = Objects.requireNonNull(RessourceDiffusableDAOLocalJsonImpl.class.getResource(appProperties.getNoGar().getLocalJsonFilePath())).toURI();
            String rawValue = Files.readString(Path.of(uri));
            log.debug(rawValue.substring(0,40));
            return  rawValue;
        } catch (URISyntaxException | IOException e) {
            throw new RessourceDiffusableDAOException(e);
        }
    }
}
