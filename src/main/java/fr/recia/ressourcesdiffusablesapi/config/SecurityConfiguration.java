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

import fr.recia.notifications.soffit_java_client.SoffitJwtAuthenticationFilter;
import fr.recia.notifications.soffit_java_client.SoffitJwtValidator;
import fr.recia.ressourcesdiffusablesapi.config.beans.SoffitProperties;
import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.soffit.security.SoffitApiAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Slf4j
@Configuration
@Profile("!test")
@EnableWebSecurity
public class SecurityConfiguration {

    private final AppProperties appProperties;

    private final SoffitProperties jwtProperties;

    public SecurityConfiguration(AppProperties appProperties, SoffitProperties jwtProperties) {
        this.appProperties = appProperties;
        this.jwtProperties = jwtProperties;
    }


    @Bean
    SoffitJwtValidator soffitJwtValidator() {
        return new SoffitJwtValidator(jwtProperties.getJwtSignatureKey());
    }

    @Bean
    SoffitJwtAuthenticationFilter soffitJwtAuthenticationFilter(SoffitJwtValidator validator) {
        return new SoffitJwtAuthenticationFilter(validator);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SoffitJwtAuthenticationFilter filter) {
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/health-check").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .anyRequest().denyAll()
        );
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new SoffitApiAuthenticationManager();
    }

}
