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
package fr.recia.ressourcesdiffusablesapi.service.cache;

import com.hazelcast.config.CacheConfig;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.List;

public class ServiceCacheHistoriqueJCache implements ServiceCacheHistorique {

    private final Cache<RessourceDiffusableFilter, List<RessourceDiffusable>> cache;
    private int size;

    public ServiceCacheHistoriqueJCache() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CacheConfig<RessourceDiffusableFilter, List<RessourceDiffusable>> configuration
                = new CacheConfig<>();
        this.cache = cacheManager.createCache("cacheRessourcesDiffusables", configuration);
        cacheManager.close();

        this.size = 0;
    }

    @Override
    public List<RessourceDiffusable> get(RessourceDiffusableFilter filter) {
        return this.cache.get(filter);
    }

    @Override
    public void put(RessourceDiffusableFilter filter, List<RessourceDiffusable> ressourcesDiffusables) {
        this.cache.put(filter, ressourcesDiffusables);

        // Mise à jour de la taille.
        this.cache.forEach(entry -> this.size++);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

}
