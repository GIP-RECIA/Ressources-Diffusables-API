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
import fr.recia.ressourcesdiffusablesapi.model.PaginationResponse;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.model.tuple.Tuple2Values;
import fr.recia.ressourcesdiffusablesapi.service.filter.IRessourceDiffusableFilterService;
import fr.recia.ressourcesdiffusablesapi.test.TestUtil;
import fr.recia.ressourcesdiffusablesapi.test.utils.RandomlyGeneratedRessourceDiffusablesForMock;
import fr.recia.ressourcesdiffusablesapi.web.rest.matchers.PaginationRequestMatcher;
import fr.recia.ressourcesdiffusablesapi.web.rest.matchers.RessourceDiffusableFilterMatcher;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@Slf4j
@WithMockUser
@WebMvcTest
class ApiRessourcesDiffusablesControllerTest {

    @MockBean
    private IRessourceDiffusableFilterService filterService;

    @Autowired
    private MockMvc mockListRessourcesMvc;

    @Test
    void testJsonApiRessourcesDiffusables() throws Exception {

        int requestPage = 1;
        int resourcesPerPage = 10;
        int responsePage = 1;
        int totalCount = 25;
        String operator = "OR";

        List<RessourceDiffusable> ressourceDiffusableList = RandomlyGeneratedRessourceDiffusablesForMock.get(10);
        PaginationResponse paginationResponse = new PaginationResponse(responsePage, totalCount );
        Tuple2Values<List<RessourceDiffusable>, PaginationResponse> tuple2Values = new Tuple2Values<>(ressourceDiffusableList, paginationResponse);

        PaginationRequest paginationRequest = new PaginationRequest(requestPage, resourcesPerPage);
        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter(operator, null,null,null,null,null,null,null,null,null, null);

        doReturn(tuple2Values).when(filterService).getRessourcesDiffusablesFiltered(argThat(new RessourceDiffusableFilterMatcher(ressourceDiffusableFilter)), argThat(new PaginationRequestMatcher(paginationRequest)));

        String stringUrlTemplate = String.format("/api/ressources-diffusables?page=%1$d&ressourcesPerPage=%2$d&operator=%3$s", requestPage, resourcesPerPage, operator);
        this.mockListRessourcesMvc.perform(get(stringUrlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())

                // Status.
                .andExpect(status().isOk())

                // Content-type.
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                // Encoding.
                .andExpect(content().encoding("UTF-8"))

                // JSON Meta Analysis.
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("timestamp")))
                .andExpect(jsonPath("$.timestamp").isNumber())
                .andExpect(jsonPath("$", Matchers.hasKey("message")))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$", Matchers.hasKey("payloadClass")))
                .andExpect(jsonPath("$.payloadClass").isString())
                .andExpect(jsonPath("$.payloadClass").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("payload")))

                // JSON Payload Analysis.
                .andExpect(jsonPath("$.payload").isMap())
                .andExpect(jsonPath("$.payload").isNotEmpty())
                .andExpect(jsonPath("$.payload", Matchers.hasKey("ressourcesDiffusables")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0]", Matchers.hasKey("ressource")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].ressource").isMap())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].ressource").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].ressource", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].ressource.id").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].ressource.id").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].ressource", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].ressource.nom").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0]", Matchers.hasKey("editeur")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].editeur").isMap())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].editeur").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].editeur", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].editeur.id").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].editeur.id").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].editeur", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].editeur.nom").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0]", Matchers.hasKey("distributeursCom")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom").isArray())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom.[0]").isMap())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom.[0]", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom.[0].id").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom.[0]", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeursCom.[0].nom").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0]", Matchers.hasKey("distributeurTech")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeurTech").isMap())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeurTech").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeurTech", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeurTech.id").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeurTech.id").isNotEmpty())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeurTech", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].distributeurTech.nom").isString())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0]", Matchers.hasKey("affichable")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].affichable").isBoolean())
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0]", Matchers.hasKey("diffusable")))
                .andExpect(jsonPath("$.payload.ressourcesDiffusables.[0].diffusable").isBoolean());
    }

    @Test
    void testJsonApiRessourcesDiffusablesBadRequest() throws Exception {
        this.mockListRessourcesMvc.perform(get("/api/ressources-diffusables?page=pokemon")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())

                // Status.
                .andExpect(status().isBadRequest())

                // Content-type.
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                // Encoding.
                .andExpect(content().encoding("UTF-8"));
        ;
    }

}
