/**
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
package fr.recia.ressourcesdiffusablesapi.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.soffit.security.SoffitApiAuthenticationManager;
import org.apereo.portal.soffit.security.SoffitApiPreAuthenticatedProcessingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.Filter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AppProperties appProperties;

    public SecurityConfiguration(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        final RequestMatcher pathMatcher = new AntPathRequestMatcher("/api/**");
        final RequestMatcher inverseMatcher = new NegatedRequestMatcher(pathMatcher);

        return web -> web.ignoring().requestMatchers(inverseMatcher);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final AbstractPreAuthenticatedProcessingFilter filter = new SoffitApiPreAuthenticatedProcessingFilter(
                appProperties.getSoffit().getJwtSignatureKey()
        );

        filter.setAuthenticationManager(authenticationManager());

        http.addFilter(filter);
        http.authorizeHttpRequests(authz -> authz
                .antMatchers("/health-check").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").authenticated()
                .anyRequest().denyAll()
        );
        http.sessionManagement().sessionFixation().newSession();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new SoffitApiAuthenticationManager();
    }

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean<Filter> disableSpringBootErrorFilter() {
        /*
         * The ErrorPageFilter (Spring) makes extra calls to HttpServletResponse.flushBuffer(),
         * and this behavior produces many warnings in the portal logs during portlet requests.
         */
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(errorPageFilter());
        filterRegistrationBean.setEnabled(false);

        return filterRegistrationBean;
    }

}
