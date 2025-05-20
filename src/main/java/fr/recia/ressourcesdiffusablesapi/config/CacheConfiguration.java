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
package fr.recia.ressourcesdiffusablesapi.config;

import fr.recia.ressourcesdiffusablesapi.service.cache.impl.CacheServiceJsonImpl;
import fr.recia.ressourcesdiffusablesapi.service.cache.ICacheService;
import fr.recia.ressourcesdiffusablesapi.service.cache.io.ICacheFileIO;
import fr.recia.ressourcesdiffusablesapi.service.cache.io.impl.CacheFileIO;
import fr.recia.ressourcesdiffusablesapi.service.dao.IRessourceDiffusableDAO;
import fr.recia.ressourcesdiffusablesapi.service.parser.IRessourceDiffusableParserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;

import java.time.Clock;

@Configuration
public class CacheConfiguration {

        private final AppProperties appProperties;
        private final IRessourceDiffusableDAO ressourceDiffusableDAO;
        private final IRessourceDiffusableParserService parserService;
        private final Clock clock;

        public CacheConfiguration(AppProperties appProperties, IRessourceDiffusableDAO ressourceDiffusableDAO, IRessourceDiffusableParserService parserService, Clock clock) {
            this.appProperties = appProperties;
            this.ressourceDiffusableDAO = ressourceDiffusableDAO;
            this.parserService = parserService;
            this.clock = clock;
        }

    @Bean
    public ICacheFileIO cacheFileIO(){
        return new CacheFileIO(appProperties);
    }

        @Bean
        public ICacheService cacheService() {
            return new CacheServiceJsonImpl(appProperties, ressourceDiffusableDAO, parserService, clock, cacheFileIO());
        }


    }