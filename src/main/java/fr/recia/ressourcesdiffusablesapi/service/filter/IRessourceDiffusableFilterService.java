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

import fr.recia.ressourcesdiffusablesapi.model.PaginationRequest;
import fr.recia.ressourcesdiffusablesapi.model.PaginationResponse;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import fr.recia.ressourcesdiffusablesapi.model.tuple.Tuple2Values;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;



public interface IRessourceDiffusableFilterService {

    public Tuple2Values<List<RessourceDiffusable>, PaginationResponse> getRessourcesDiffusablesFiltered(RessourceDiffusableFilter ressourceDiffusableFilter, PaginationRequest paginationRequest);


}
