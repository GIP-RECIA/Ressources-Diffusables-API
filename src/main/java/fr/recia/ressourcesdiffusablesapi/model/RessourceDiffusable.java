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

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

import static fr.recia.ressourcesdiffusablesapi.utils.Utils.emptyIfNull;
import static fr.recia.ressourcesdiffusablesapi.utils.Utils.falseIfNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RessourceDiffusable implements Serializable {

    @Getter
    private final AttributRessource ressource;
    @Getter
    private final AttributRessource editeur;
    @Getter
    private final Collection<AttributRessource> distributeursCom;
    @Getter
    private final AttributRessource distributeurTech;
    @Getter
    private final boolean affichable;
    @Getter
    private final boolean diffusable;
    @Getter
    private final boolean mereFamille;
    @Getter
    private final String membreFamille;


    public RessourceDiffusable(
            AttributRessource ressource,
            AttributRessource editeur,
            Collection<AttributRessource> distributeursCom,
            AttributRessource distributeurTech,
            boolean affichable,
            boolean diffusable, boolean mereFamille, String membreFamille
    ) {
        this.ressource = ressource;
        this.editeur = editeur;
        this.distributeursCom = emptyIfNull(distributeursCom);
        this.distributeurTech = distributeurTech;
        this.affichable = affichable;
        this.diffusable = diffusable;
        this.mereFamille = mereFamille;
        this.membreFamille = membreFamille;
    }

    @JsonCreator
    public RessourceDiffusable(@JsonProperty("idRessource") String idRessource,
                               @JsonProperty("nomRessource") String nomRessource,
                               @JsonProperty("idEditeur") String idEditeur,
                               @JsonProperty("nomEditeur") String nomEditeur,
                               @JsonProperty("distributeurTech") String idDistributeurTech,
                               @JsonProperty("nomDistributeurTech") String nomDistributeurTech,
                               @JsonProperty("membreFamille") String membreFamille,
                               @JsonProperty("distributeursCom") Collection<AttributRessource> distributeursCom,
                               @JsonProperty("affichable") Boolean affichable,
                               @JsonProperty("diffusable") Boolean diffusable,
                               @JsonProperty("mereFamille") Boolean mereFamille){
        this.ressource = new AttributRessource(idRessource, nomRessource);
        this.editeur = new AttributRessource(idEditeur, nomEditeur);
        this.distributeursCom = emptyIfNull(distributeursCom);
        this.distributeurTech = new AttributRessource(idDistributeurTech, nomDistributeurTech);
        this.affichable = falseIfNull(affichable);
        this.diffusable = falseIfNull(diffusable);
        this.mereFamille = falseIfNull(mereFamille);
        this.membreFamille = emptyIfNull(membreFamille);
    }

    @JsonIgnore @Getter
    private final Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
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
