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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceCacheHistorique {

    /**
     * Retourne la liste de ressources diffusables correspondant au filtre si cette dernière a été mise en cache.
     *
     * @param filter Filtre pour lequel on recherche la liste correspondante
     * @return La liste de ressources diffusables, ou null si ce filtre ne correspond à aucune liste mise en cache
     */
    List<RessourceDiffusable> get(RessourceDiffusableFilter filter);

    /**
     * Met en cache la liste de ressources diffusables au filtre correspondant.
     *
     * @param filter                Filtre clé
     * @param ressourcesDiffusables Liste de ressources diffusables
     */
    void put(RessourceDiffusableFilter filter, List<RessourceDiffusable> ressourcesDiffusables);

    /**
     * Retourne le nombre de listes de ressources diffusables actuellement en cache.
     *
     * @return Nombre de listes de ressources diffusables actuellement en cache
     */
    int size();

    /**
     * Vide entièrement le cache en supprimant toutes les listes de ressources diffusables qu'il contient.
     */
    void clear();
}
