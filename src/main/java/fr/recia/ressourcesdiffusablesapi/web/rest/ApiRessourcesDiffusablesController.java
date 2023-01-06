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
package fr.recia.ressourcesdiffusablesapi.web.rest;

import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.model.apiresponse.ApiError;
import fr.recia.ressourcesdiffusablesapi.model.apiresponse.ApiResponse;
import fr.recia.ressourcesdiffusablesapi.service.gar.ServiceGar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping(path = "api/")
public class ApiRessourcesDiffusablesController {

    @Autowired
    private ServiceGar serviceGar;

    @GetMapping(value = "/ressources-diffusables")
    public ApiResponse ressourcesDiffusables(
            @RequestParam(value = "page") final int page,
            @RequestParam(value = "operator", required = false) final String operator,
            @RequestParam(value = "idRessource", required = false) final String idRessource,
            @RequestParam(value = "nomRessource", required = false) final String nomRessource,
            @RequestParam(value = "idEditeur", required = false) final String idEditeur,
            @RequestParam(value = "nomEditeur", required = false) final String nomEditeur,
            @RequestParam(value = "distributeurCom", required = false) final String distributeurCom,
            @RequestParam(value = "nomDistributeurCom", required = false) final String nomDistributeurCom,
            @RequestParam(value = "distributeurTech", required = false) final String distributeurTech,
            @RequestParam(value = "nomDistributeurTech", required = false) final String nomDistributeurTech,
            @RequestParam(value = "affichable", required = false) final Boolean affichable,
            @RequestParam(value = "diffusable", required = false) final Boolean diffusable,
            @RequestParam(value = "ressourcesPerPage", defaultValue = "32") final int elementsParPage,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (log.isDebugEnabled()) log.debug("Ressources diffusables request.");
        return new ApiResponse(
                "Ressources diffusables request successful.",
                this.serviceGar.getRessourcesDiffusables(
                        page,
                        elementsParPage,
                        new RessourceDiffusableFilter(
                                operator,
                                idRessource,
                                nomRessource,
                                idEditeur,
                                nomEditeur,
                                distributeurCom,
                                nomDistributeurCom,
                                distributeurTech,
                                nomDistributeurTech,
                                affichable,
                                diffusable
                        )
                )
        );
    }

    @GetMapping(value = "/ressources-diffusables/page-count")
    public ApiResponse pageCount(
            @RequestParam(value = "operator", required = false) final String operator,
            @RequestParam(value = "idRessource", required = false) final String idRessource,
            @RequestParam(value = "nomRessource", required = false) final String nomRessource,
            @RequestParam(value = "idEditeur", required = false) final String idEditeur,
            @RequestParam(value = "nomEditeur", required = false) final String nomEditeur,
            @RequestParam(value = "distributeurCom", required = false) final String distributeurCom,
            @RequestParam(value = "nomDistributeurCom", required = false) final String nomDistributeurCom,
            @RequestParam(value = "distributeurTech", required = false) final String distributeurTech,
            @RequestParam(value = "nomDistributeurTech", required = false) final String nomDistributeurTech,
            @RequestParam(value = "affichable", required = false) final Boolean affichable,
            @RequestParam(value = "diffusable", required = false) final Boolean diffusable,
            @RequestParam(value = "ressourcesPerPage", defaultValue = "32") final int elementsParPage,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (log.isDebugEnabled()) log.debug("Nombre pages ressources diffusables request.");
        return new ApiResponse(
                "Ressources diffusables page count request successful.",
                this.serviceGar.getPageCount(
                        elementsParPage,
                        new RessourceDiffusableFilter(
                                operator,
                                idRessource,
                                nomRessource,
                                idEditeur,
                                nomEditeur,
                                distributeurCom,
                                nomDistributeurCom,
                                distributeurTech,
                                nomDistributeurTech,
                                affichable,
                                diffusable
                        )
                )
        );
    }

    @GetMapping(value = "/ressources-diffusables/size")
    public ApiResponse size(
            @RequestParam(value = "operator", required = false) final String operator,
            @RequestParam(value = "idRessource", required = false) final String idRessource,
            @RequestParam(value = "nomRessource", required = false) final String nomRessource,
            @RequestParam(value = "idEditeur", required = false) final String idEditeur,
            @RequestParam(value = "nomEditeur", required = false) final String nomEditeur,
            @RequestParam(value = "distributeurCom", required = false) final String distributeurCom,
            @RequestParam(value = "nomDistributeurCom", required = false) final String nomDistributeurCom,
            @RequestParam(value = "distributeurTech", required = false) final String distributeurTech,
            @RequestParam(value = "nomDistributeurTech", required = false) final String nomDistributeurTech,
            @RequestParam(value = "affichable", required = false) final Boolean affichable,
            @RequestParam(value = "diffusable", required = false) final Boolean diffusable,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (log.isDebugEnabled()) log.debug("Nombre éléments ressources diffusables request.");
        return new ApiResponse(
                "Ressources diffusables list size request successful.",
                this.serviceGar.getSize(
                        new RessourceDiffusableFilter(
                                operator,
                                idRessource,
                                nomRessource,
                                idEditeur,
                                nomEditeur,
                                distributeurCom,
                                nomDistributeurCom,
                                distributeurTech,
                                nomDistributeurTech,
                                affichable,
                                diffusable
                        )
                )
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ApiResponse handleExceptionMissingParameter(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        return new ApiResponse(
                "Api request failed: bad request.",
                new ApiError(exception)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ApiResponse handleExceptionElse(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        return new ApiResponse(
                "Api request failed: unknown internal server error.",
                new ApiError(exception)
        );
    }
}
