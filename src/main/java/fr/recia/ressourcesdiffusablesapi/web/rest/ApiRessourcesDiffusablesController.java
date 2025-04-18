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
package fr.recia.ressourcesdiffusablesapi.web.rest;

import fr.recia.ressourcesdiffusablesapi.model.PaginationRequest;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.model.apiresponse.ApiResponse;
import fr.recia.ressourcesdiffusablesapi.service.filter.IRessourceDiffusableFilterService;
import fr.recia.ressourcesdiffusablesapi.web.rest.exceptions.RequestArgumentNumericValueInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping(path = "api/")
public class ApiRessourcesDiffusablesController {

    private  final IRessourceDiffusableFilterService filterService;

    public ApiRessourcesDiffusablesController(IRessourceDiffusableFilterService ressourceDiffusableFilterService) {
        this.filterService = ressourceDiffusableFilterService;
    }

    @GetMapping(value = "/ressources-diffusables")
    public ResponseEntity<ApiResponse> ressourcesDiffusables(
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

        if(page <= 0){
            throw new RequestArgumentNumericValueInvalidException(String.format("page number is %d, should be greater than zero", page));
        }
        if(elementsParPage <= 0){
            throw new RequestArgumentNumericValueInvalidException(String.format("elementsParPage (\"ressourcesPerPage\" in  request) is %d, should be greater than zero", page));
        }

        RessourceDiffusableFilter filter = new RessourceDiffusableFilter(
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
        );
        return ResponseEntity.ok(new ApiResponse("Ressources diffusables request successful.", filterService.getRessourcesDiffusablesFiltered(filter,
                new PaginationRequest(page,elementsParPage))));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ResponseEntity<String>  handleExceptionMissingParameter(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        return  ResponseEntity.badRequest().body("Api request failed: bad request.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ResponseEntity<String>  handleExceptionElse(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
         return ResponseEntity.internalServerError().body("Api request failed: unknown internal server error.");
    }

}
