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
package fr.recia.ressourcesdiffusablesapi.web.rest.matchers;

import fr.recia.ressourcesdiffusablesapi.model.PaginationRequest;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

public class PaginationRequestMatcher implements ArgumentMatcher<PaginationRequest> {

    private PaginationRequest left;

    public PaginationRequestMatcher(PaginationRequest paginationRequest){
        Objects.requireNonNull(paginationRequest);
        this.left = paginationRequest;
    }

    @Override
    public boolean matches(PaginationRequest right) {
        Objects.requireNonNull(right);
        return Objects.equals(left.getObjectsPerPage(), right.getObjectsPerPage()) && Objects.equals(left.getPageIndexHumanReadable(), right.getPageIndexHumanReadable()) ;
    }
}
