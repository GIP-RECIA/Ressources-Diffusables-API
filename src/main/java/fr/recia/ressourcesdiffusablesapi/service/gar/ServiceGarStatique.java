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
package fr.recia.ressourcesdiffusablesapi.service.gar;

import fr.recia.ressourcesdiffusablesapi.model.AttributRessource;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ServiceGarStatique implements ServiceGar {

    private final List<RessourceDiffusable> ressourcesDiffusablesComplet = new ArrayList<>();

    @Override
    public int getSize(RessourceDiffusableFilter filter) {
        return this.ressourcesDiffusablesComplet.size();
    }

    @Override
    public int getPageCount(int elementsParPage, RessourceDiffusableFilter filter) {
        if (this.ressourcesDiffusablesComplet.isEmpty()) this.initialize();

        if (filter.isEmpty()) {
            return (int) Math.ceil(this.ressourcesDiffusablesComplet.size() / (double) elementsParPage);
        } else {
            List<RessourceDiffusable> ressourcesDiffusablesFiltrees = new ArrayList<>();
            for (RessourceDiffusable ressourceDiffusable : this.ressourcesDiffusablesComplet) {
                if (filter.filter(ressourceDiffusable)) ressourcesDiffusablesFiltrees.add(ressourceDiffusable);
            }

            return (int) Math.ceil(ressourcesDiffusablesFiltrees.size() / (double) elementsParPage);
        }
    }

    @Override
    public Collection<RessourceDiffusable> getRessourcesDiffusables(int page, int elementsParPage, RessourceDiffusableFilter filter) {
        if (this.ressourcesDiffusablesComplet.isEmpty()) this.initialize();

        List<RessourceDiffusable> ressourcesDiffusablesFiltrees = new ArrayList<>();
        for (RessourceDiffusable ressourceDiffusable : this.ressourcesDiffusablesComplet) {
            if (filter.filter(ressourceDiffusable)) ressourcesDiffusablesFiltrees.add(ressourceDiffusable);
        }

        return ressourcesDiffusablesFiltrees;
    }

    private void initialize() {
        for (int i = 0; i < 100; i++) {
            this.ajouterRessource(
                    new RessourceDiffusable(
                            new AttributRessource("idRessource." + i, "nomRessource." + i),
                            new AttributRessource("idEditeur." + i, ""),
                            Arrays.asList(
                                    new AttributRessource("distributeurCom." + i + ".1", ""),
                                    new AttributRessource("distributeurCom." + i + ".2", "")
                            ),
                            new AttributRessource("distributeurTech." + i, ""),
                            true,
                            true
                    )
            );
        }
    }

    private void ajouterRessource(RessourceDiffusable ressourceDiffusable) {
        this.ressourcesDiffusablesComplet.add(ressourceDiffusable);
    }

}
