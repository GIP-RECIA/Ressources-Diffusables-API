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
package fr.recia.ressourcesdiffusablesapi.service.cache;

import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServiceCacheHistoriqueMap implements ServiceCacheHistorique {

    private final LinkedHashMap<RessourceDiffusableFilter, List<RessourceDiffusable>> historiqueRequetes = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            return super.size() > 10;
        }
    };

    @Override
    public List<RessourceDiffusable> get(RessourceDiffusableFilter filter) {
        return this.historiqueRequetes.get(filter);
    }

    @Override
    public void put(RessourceDiffusableFilter filter, List<RessourceDiffusable> ressourcesDiffusables) {
        if (log.isDebugEnabled())
            log.debug("Cache: Ressource diffusable put in history using HashMap; new size of history is " + this.historiqueRequetes.size());
        this.historiqueRequetes.put(filter, ressourcesDiffusables);
    }

    @Override
    public int size() {
        return this.historiqueRequetes.size();
    }

    @Override
    public void clear() {
        int count = this.historiqueRequetes.size();
        this.historiqueRequetes.clear();
        if (log.isDebugEnabled()) log.debug("Cache: History cleared using HashMap; " + count + " elements removed");
    }
}
