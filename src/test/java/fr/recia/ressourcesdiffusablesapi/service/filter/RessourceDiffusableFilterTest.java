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
package fr.recia.ressourcesdiffusablesapi.service.filter;

import fr.recia.ressourcesdiffusablesapi.config.TestSecurityConfiguration;
import fr.recia.ressourcesdiffusablesapi.model.PaginationRequest;
import fr.recia.ressourcesdiffusablesapi.model.PaginationResponse;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.model.tuple.Tuple2Values;
import fr.recia.ressourcesdiffusablesapi.service.cache.ICacheService;
import fr.recia.ressourcesdiffusablesapi.service.filter.impl.RessourceDiffusableFilterServiceDefaultImpl;
import fr.recia.ressourcesdiffusablesapi.test.utils.RessourceDiffusableForMockFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig
@SpringBootTest
public class RessourceDiffusableFilterTest {

    @MockitoBean
    ICacheService cacheService;

    @Autowired
    RessourceDiffusableFilterServiceDefaultImpl filterService;

    @Test
    public void filterRetrieveAllMatchingResources(){
        int listAnglaisSize = 5;
        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>();
        List<RessourceDiffusable> ressourceDiffusablesAnglaisList = RessourceDiffusableForMockFactory.argAsAllValues("Anglais", false, listAnglaisSize);

        ressourceDiffusableList.addAll(ressourceDiffusablesAnglaisList);
        ressourceDiffusableList.addAll(RessourceDiffusableForMockFactory.argAsAllValues("Maths", false, 8));

        doReturn(ressourceDiffusableList).when(cacheService).getRessourceDiffusableList();

        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter("OR",
                "Anglais","Anglais","Anglais","Anglais", "Anglais","Anglais","Anglais","Anglais", null, null);

        PaginationRequest paginationRequest = new PaginationRequest(1, Integer.MAX_VALUE);

        Tuple2Values<List<RessourceDiffusable>, PaginationResponse> response =  filterService.getRessourcesDiffusablesFiltered(ressourceDiffusableFilter, paginationRequest);

        assertEquals(listAnglaisSize, response.getObject1().size());
        assertTrue(response.getObject1().containsAll(ressourceDiffusablesAnglaisList));
    }

    @Test
    public void filterUsePagination(){
        int resourcesPerPage = 10;
        int paginationIndexHumanReadable = 1;
        int paginationIndex = 0;
        int resourcesTotal = 43;

        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(resourcesTotal);
        ressourceDiffusableList.addAll(RessourceDiffusableForMockFactory.argAsAllValues("Anglais", false, resourcesTotal));

        doReturn(ressourceDiffusableList).when(cacheService).getRessourceDiffusableList();

        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter("OR", "","","","","","","","",null, null);

        PaginationRequest paginationRequest = new PaginationRequest(paginationIndexHumanReadable,resourcesPerPage);

        List<RessourceDiffusable> expectedResult = ressourceDiffusableList.subList(paginationIndex, paginationIndex+resourcesPerPage);

        Tuple2Values<List<RessourceDiffusable>, PaginationResponse> response =  filterService.getRessourcesDiffusablesFiltered(ressourceDiffusableFilter, paginationRequest);


        assertEquals(expectedResult.size(), response.getObject1().size());
        assertTrue(expectedResult.containsAll(response.getObject1()));
    }

    @Test
    public void getLastPageThatAsLessResourcesThanOtherPages(){
        int resourcesPerPage = 10;
        int resourcesTotal = 43;
        int maxPage = (int) Math.ceil ((double)resourcesTotal / (double)resourcesPerPage);
        int paginationIndexHumanReadable = maxPage;
        System.out.println("max page "+maxPage);

        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(resourcesTotal);
        ressourceDiffusableList.addAll(RessourceDiffusableForMockFactory.argAsAllValues("Anglais", false, resourcesTotal));

        doReturn(ressourceDiffusableList).when(cacheService).getRessourceDiffusableList();

        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter("OR", "","","","","","","","",null, null);

        PaginationRequest paginationRequest = new PaginationRequest(paginationIndexHumanReadable,resourcesPerPage);

        //the last 3, at index 40, 41, 42
        List<RessourceDiffusable> expectedResult = ressourceDiffusableList.subList(40, 43);

        Tuple2Values<List<RessourceDiffusable>, PaginationResponse> response =  filterService.getRessourcesDiffusablesFiltered(ressourceDiffusableFilter, paginationRequest);

        assertEquals(expectedResult.size(), response.getObject1().size());
        assertTrue(expectedResult.containsAll(response.getObject1()));
    }


    @Test
    public void pageIndexTooHighReturnLastPage(){
        int resourcesPerPage = 10;
        int paginationIndexHumanReadable = 1000;
        int resourcesTotal = 43;

        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(resourcesTotal);
        ressourceDiffusableList.addAll(RessourceDiffusableForMockFactory.argAsAllValues("Anglais", false, resourcesTotal));

        doReturn(ressourceDiffusableList).when(cacheService).getRessourceDiffusableList();

        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter("OR", "","","","","","","","",null, null);

        PaginationRequest paginationRequest = new PaginationRequest(paginationIndexHumanReadable,resourcesPerPage);

        //the last 3, at index 40, 41, 42
        List<RessourceDiffusable> expectedResult = ressourceDiffusableList.subList(40, 43);

        Tuple2Values<List<RessourceDiffusable>, PaginationResponse> response =  filterService.getRessourcesDiffusablesFiltered(ressourceDiffusableFilter, paginationRequest);

        assertEquals(expectedResult.size(), response.getObject1().size());
        assertTrue(expectedResult.containsAll(response.getObject1()));
    }




    @Test
    public void negativePageIndex_KO(){
        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(1);
        ressourceDiffusableList.addAll(RessourceDiffusableForMockFactory.argAsAllValues("Anglais", false, 1));

        doReturn(ressourceDiffusableList).when(cacheService).getRessourceDiffusableList();

        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter("OR", "","","","","","","","",null, null);

        PaginationRequest paginationRequest = new PaginationRequest(-1,10);

        assertThrows(RuntimeException.class, ()-> {filterService.getRessourcesDiffusablesFiltered(ressourceDiffusableFilter, paginationRequest);});
    }

    @Test
    public void negativeResourcesPerPage_KO(){
        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(1);
        ressourceDiffusableList.addAll(RessourceDiffusableForMockFactory.argAsAllValues("Anglais", false, 1));

        doReturn(ressourceDiffusableList).when(cacheService).getRessourceDiffusableList();

        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter("OR", "","","","","","","","",null, null);

        PaginationRequest paginationRequest = new PaginationRequest(1,-10);

        assertThrows(RuntimeException.class, ()-> {filterService.getRessourcesDiffusablesFiltered(ressourceDiffusableFilter, paginationRequest);});
    }

    @Test
    public void zeroResourcesPerPage_KO(){
        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(1);
        ressourceDiffusableList.addAll(RessourceDiffusableForMockFactory.argAsAllValues("Anglais", false, 1));

        doReturn(ressourceDiffusableList).when(cacheService).getRessourceDiffusableList();

        RessourceDiffusableFilter ressourceDiffusableFilter = new RessourceDiffusableFilter("OR", "","","","","","","","",null, null);

        PaginationRequest paginationRequest = new PaginationRequest(1,0);

        assertThrows(RuntimeException.class, ()-> {filterService.getRessourcesDiffusablesFiltered(ressourceDiffusableFilter, paginationRequest);});
    }

}
