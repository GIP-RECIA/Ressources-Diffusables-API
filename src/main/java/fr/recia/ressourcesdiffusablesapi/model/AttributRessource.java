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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

import static fr.recia.ressourcesdiffusablesapi.utils.Utils.emptyIfNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributRessource implements Serializable {

    @JsonAlias({"distributeurCom"})
    private final String id;
    @JsonAlias({"nomDistributeurCom"})
    private final String nom;

    public AttributRessource(String id, String nom) {
        this.id = emptyIfNull(id);
        this.nom = emptyIfNull(nom);
    }

    public AttributRessource(){
        this.id = "";
        this.nom = "";
    }

    public AttributRessource copy(){
        return new AttributRessource(this.id, this.nom);
    }

    public String getId() {
        return this.id;
    }

    public String getNom() {
        return this.nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributRessource that = (AttributRessource) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
