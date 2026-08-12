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

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@Slf4j
public class TestSecurityConfiguration {

    @Bean
    SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/health-check").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    ApplicationRunner securityChainsLogger(FilterChainProxy filterChainProxy) {
        return args -> {
            log.info("SECURITY FILTER CHAINS:");
            System.out.println("SECURITY FILTER CHAINS:");
            filterChainProxy.getFilterChains().forEach(chain -> {
                log.info("{}", chain);
                System.out.println(chain.toString());
                    }

            );
        };
    }
}
