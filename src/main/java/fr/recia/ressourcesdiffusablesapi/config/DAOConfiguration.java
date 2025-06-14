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

import fr.recia.ressourcesdiffusablesapi.service.dao.IRessourceDiffusableDAO;
import fr.recia.ressourcesdiffusablesapi.service.dao.impl.RessourceDiffusableDAOHttpImpl;
import fr.recia.ressourcesdiffusablesapi.service.dao.impl.RessourceDiffusableDAOLocalJsonImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class DAOConfiguration {

    private final AppProperties appProperties;

    public DAOConfiguration(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public RestTemplate restTemplate() {
      return new RestTemplate();
    }

    @ConditionalOnProperty(name="app.use-gar", havingValue="true")
    @Bean(name="rdDAO")
    public IRessourceDiffusableDAO httpGetImpl() {
        return new RessourceDiffusableDAOHttpImpl(appProperties);
    }

    @ConditionalOnMissingBean(IRessourceDiffusableDAO.class)
    @Bean(name="rdDAO")
    public IRessourceDiffusableDAO localFileImpl() {
        return new RessourceDiffusableDAOLocalJsonImpl(appProperties);
    }

}
