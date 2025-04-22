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
package fr.recia.ressourcesdiffusablesapi.service.filter.impl;

import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.model.PaginationRequest;
import fr.recia.ressourcesdiffusablesapi.model.PaginationResponse;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.model.tuple.Tuple2Values;
import fr.recia.ressourcesdiffusablesapi.service.cache.ICacheService;
import fr.recia.ressourcesdiffusablesapi.service.filter.IRessourceDiffusableFilterService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j

public class RessourceDiffusableFilterServiceDefaultImpl implements IRessourceDiffusableFilterService {

    private final ICacheService cacheService;

    public RessourceDiffusableFilterServiceDefaultImpl(ICacheService cacheService){

        this.cacheService = cacheService;
    }

    @Override
    public Tuple2Values<List<RessourceDiffusable>, PaginationResponse> getRessourcesDiffusablesFiltered(RessourceDiffusableFilter ressourceDiffusableFilter, PaginationRequest paginationRequest) {
        List<RessourceDiffusable> ressourceDiffusableListFiltered = new ArrayList<>();
        for (RessourceDiffusable current : cacheService.getRessourceDiffusableList()) {
            if (ressourceDiffusableFilter.filter(current)) {
                ressourceDiffusableListFiltered.add(current);
            }
        }

        int filteredListSize = ressourceDiffusableListFiltered.size();

        if(filteredListSize == 0){
            return new Tuple2Values<>(new ArrayList<>(), new PaginationResponse(-1,filteredListSize));
        }

        int possiblePagesCount = filteredListSize / paginationRequest.getObjectsPerPage()  + ((filteredListSize % paginationRequest.getObjectsPerPage() == 0)? 0 : 1 );
        //int n = a / b + ((a % b == 0) ? 0 : 1);
        int firstIndexToUse = (paginationRequest.getPageIndexHumanReadable() - 1) * paginationRequest.getObjectsPerPage();
        firstIndexToUse = Math.min(firstIndexToUse, (possiblePagesCount -1) * paginationRequest.getObjectsPerPage());

        // (possiblePagesCount -1) * paginationRequest.getObjectsPerPage() is the index of the first item of the last page
        // if firstIndexToUse is bigger than that index, we must use that index instead
        // so if the required page does not exist, we return the last page instead of throwing an exception

        int lastIndexToUse = firstIndexToUse + paginationRequest.getObjectsPerPage() -1;
        // last index must the equals to the number of objects per page, minus one, i.e. 20 objects per page, first page is 0 -> 19
        lastIndexToUse = Math.min(lastIndexToUse, filteredListSize -1);
        // if lastIndexToUse is bigger than the last possible index of the filtered list, we use the last possible index instead

        int constructorArgPageIndex = (firstIndexToUse / paginationRequest.getObjectsPerPage()) + 1;
        // +1 because we return it in human-readable format

        PaginationResponse paginationResponse = new PaginationResponse(constructorArgPageIndex,filteredListSize);

        // +1 because second argument is exclusive but we want to include it
        return new Tuple2Values<>(ressourceDiffusableListFiltered.subList(firstIndexToUse,lastIndexToUse+1), paginationResponse);
    }
}
