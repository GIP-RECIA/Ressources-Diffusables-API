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

import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

public class RessourceDiffusableFilterMatcher implements ArgumentMatcher<RessourceDiffusableFilter> {

    private RessourceDiffusableFilter left;

    public RessourceDiffusableFilterMatcher(RessourceDiffusableFilter ressourceDiffusableFilter){
        Objects.requireNonNull(ressourceDiffusableFilter);
        this.left = ressourceDiffusableFilter;
    }

    @Override
    public boolean matches(RessourceDiffusableFilter right) {
        Objects.requireNonNull(right);
        if(!Objects.equals(left.getIdRessource(), right.getIdRessource()))
            return false;
        if(!Objects.equals(left.getNomRessource(), right.getNomRessource()))
            return false;
        if(!Objects.equals(left.getIdEditeur(), right.getIdEditeur()))
            return false;
        if(!Objects.equals(left.getNomEditeur(), right.getNomEditeur()))
            return false;
        if(!Objects.equals(left.getDistributeurCom(), right.getDistributeurCom()))
            return false;
        if(!Objects.equals(left.getNomDistributeurCom(), right.getNomDistributeurCom()))
            return false;
        if(!Objects.equals(left.getDistributeurTech(), right.getDistributeurTech()))
            return false;
        if(!Objects.equals(left.getNomDistributeurTech(), right.getNomDistributeurTech()))
            return false;
        if(!Objects.equals(left.getOperator(), right.getOperator()))
            return false;
        if(!Objects.equals(left.isDiffusable(), right.isDiffusable()))
            return false;
        if(!Objects.equals(left.isAffichable(), right.isAffichable()))
            return false;
        return true;
    }
}
