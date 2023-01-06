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
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.List;

@Slf4j
public class ServiceCacheHistoriqueEhcache implements ServiceCacheHistorique {

    Cache cache = CacheManager.getInstance().getCache("cacheRessourcesDiffusables");

    @Override
    public List<RessourceDiffusable> get(RessourceDiffusableFilter filter) {
        Element element = this.cache.get(filter);

        return element == null ? null : (List<RessourceDiffusable>) element.getObjectValue();
    }

    @Override
    public void put(RessourceDiffusableFilter filter, List<RessourceDiffusable> ressourcesDiffusables) {
        this.cache.put(new Element(filter, ressourcesDiffusables));
        if (log.isDebugEnabled())
            log.debug("Cache: Ressource diffusable put in history using Ehcache; new size of history is " + this.cache.getSize());
    }

    @Override
    public int size() {
        return CacheManager.getInstance().getCache("cacheRessourcesDiffusables").getSize();
    }

    @Override
    public void clear() {
        int count = this.cache.getSize();
        this.cache.removeAll();
        if (log.isDebugEnabled()) log.debug("Cache: History cleared using Ehcache; " + count + " elements removed");
    }
}
