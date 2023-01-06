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
package fr.recia.ressourcesdiffusablesapi.service.gar;

import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface ServiceGar {

    /**
     * Retourne le nombre de ressources diffusables sélectionnées par le filtre.
     *
     * @param filter Filtre de la requête
     * @return le nombre de ressources diffusables sélectionnées par le filtre
     */
    int getSize(RessourceDiffusableFilter filter);

    /**
     * Retourne le nombre de pages de ressources diffusables sélectionnées par le filtre.
     *
     * @param elementsParPage Nombre d'éléments dans chaque page
     * @param filter          Filtre de la requête
     * @return le nombre de pages de ressources diffusables sélectionnées par le filtre
     */
    int getPageCount(int elementsParPage, RessourceDiffusableFilter filter);

    /**
     * Retourne une collection des ressources diffusables sélectionnées par le filtre. La collection correspond à une
     * portion de la liste complète des ressources diffusables sélectionnées qui est paginée. La première page est la
     * page 0.
     *
     * @param page            Numéro de page demandé
     * @param elementsParPage Nombre d'éléments dans chaque page
     * @param filter          Filtre de la requête
     * @return une collection des ressources diffusables sélectionnées par le filtre
     */
    Collection<RessourceDiffusable> getRessourcesDiffusables(int page, int elementsParPage, RessourceDiffusableFilter filter);
}
