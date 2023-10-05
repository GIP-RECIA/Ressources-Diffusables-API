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
package fr.recia.ressourcesdiffusablesapi.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class RessourceDiffusable implements Serializable {

    private final AttributRessource ressource;
    private final AttributRessource editeur;
    private final Collection<AttributRessource> distributeursCom;
    private final AttributRessource distributeurTech;
    private final boolean affichable;
    private final boolean diffusable;

    public RessourceDiffusable(
            AttributRessource ressource,
            AttributRessource editeur,
            Collection<AttributRessource> distributeursCom,
            AttributRessource distributeurTech,
            boolean affichable,
            boolean diffusable
    ) {
        this.ressource = ressource;
        this.editeur = editeur;
        this.distributeursCom = distributeursCom;
        this.distributeurTech = distributeurTech;
        this.affichable = affichable;
        this.diffusable = diffusable;
    }

    public AttributRessource getRessource() {
        return this.ressource;
    }

    public AttributRessource getEditeur() {
        return this.editeur;
    }

    public Collection<AttributRessource> getDistributeursCom() {
        return this.distributeursCom;
    }

    public AttributRessource getDistributeurTech() {
        return this.distributeurTech;
    }

    public boolean isAffichable() {
        return this.affichable;
    }

    public boolean isDiffusable() {
        return this.diffusable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RessourceDiffusable that = (RessourceDiffusable) o;

        return affichable == that.affichable && diffusable == that.diffusable && ressource.equals(that.ressource) && editeur.equals(that.editeur) && distributeursCom.equals(that.distributeursCom) && distributeurTech.equals(that.distributeurTech);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ressource, editeur, distributeursCom, distributeurTech, affichable, diffusable);
    }

}
