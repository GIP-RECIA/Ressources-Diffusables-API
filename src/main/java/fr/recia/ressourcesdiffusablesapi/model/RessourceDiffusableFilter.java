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

import fr.recia.ressourcesdiffusablesapi.enums.FilterBoolean;
import fr.recia.ressourcesdiffusablesapi.utils.Utils;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Filter;

public class RessourceDiffusableFilter implements Serializable {

    private enum Operator {AND, OR}

    private final Operator operator;
    private final String idRessource;
    private final String nomRessource;
    private final String idEditeur;
    private final String nomEditeur;
    private final String distributeurCom;
    private final String nomDistributeurCom;
    private final String distributeurTech;
    private final String nomDistributeurTech;
    private final FilterBooleanWrapper affichable;
    private final FilterBooleanWrapper diffusable;

    public RessourceDiffusableFilter(
            String operator,
            String idRessource,
            String nomRessource,
            String idEditeur,
            String nomEditeur,
            String distributeurCom,
            String nomDistributeurCom,
            String distributeurTech,
            String nomDistributeurTech,
            Boolean affichable,
            Boolean diffusable
    ) {
        this.operator = operator == null ? Operator.AND : operatorByName(operator.toUpperCase(Locale.ROOT));
        this.idRessource = idRessource == null ? null : unaccent(idRessource.toLowerCase(Locale.ROOT));
        this.nomRessource = nomRessource == null ? null : unaccent(nomRessource.toLowerCase(Locale.ROOT));
        this.idEditeur = idEditeur == null ? null : unaccent(idEditeur.toLowerCase(Locale.ROOT));
        this.nomEditeur = nomEditeur == null ? null : unaccent(nomEditeur.toLowerCase(Locale.ROOT));
        this.distributeurCom = distributeurCom == null ? null : unaccent(distributeurCom.toLowerCase(Locale.ROOT));
        this.nomDistributeurCom = nomDistributeurCom == null ? null : unaccent(nomDistributeurCom.toLowerCase(Locale.ROOT));
        this.distributeurTech = distributeurTech == null ? null : unaccent(distributeurTech.toLowerCase(Locale.ROOT));
        this.nomDistributeurTech = nomDistributeurTech == null ? null : unaccent(nomDistributeurTech.toLowerCase(Locale.ROOT));
        this.affichable = new FilterBooleanWrapper(affichable);
        this.diffusable = new FilterBooleanWrapper(diffusable);
    }

    public Operator getOperator() {
        return this.operator;
    }

    public String getIdRessource() {
        return this.idRessource;
    }

    public String getNomRessource() {
        return this.nomRessource;
    }

    public String getIdEditeur() {
        return this.idEditeur;
    }

    public String getNomEditeur() {
        return this.nomEditeur;
    }

    public String getDistributeurCom() {
        return this.distributeurCom;
    }

    public String getNomDistributeurCom() {
        return this.nomDistributeurCom;
    }

    public String getDistributeurTech() {
        return this.distributeurTech;
    }

    public String getNomDistributeurTech() {
        return this.nomDistributeurTech;
    }

    public FilterBooleanWrapper isAffichable() {
        return this.affichable;
    }

    public FilterBooleanWrapper isDiffusable() {
        return this.diffusable;
    }

    private static Operator operatorByName(String name) {
        for (Operator operator : Operator.values()) {
            if (name.equals(operator.name())) {
                return operator;
            }
        }

        return Operator.AND;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RessourceDiffusableFilter that = (RessourceDiffusableFilter) o;

        return operator == that.operator && Objects.equals(idRessource, that.idRessource) && Objects.equals(nomRessource, that.nomRessource) && Objects.equals(idEditeur, that.idEditeur) && Objects.equals(nomEditeur, that.nomEditeur) && Objects.equals(distributeurCom, that.distributeurCom) && Objects.equals(nomDistributeurCom, that.nomDistributeurCom) && Objects.equals(distributeurTech, that.distributeurTech) && Objects.equals(nomDistributeurTech, that.nomDistributeurTech) && Objects.equals(affichable, that.affichable) && Objects.equals(diffusable, that.diffusable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, idRessource, nomRessource, idEditeur, nomEditeur, distributeurCom, nomDistributeurCom, distributeurTech, nomDistributeurTech, affichable, diffusable);
    }

    private static String unaccent(String string) {
        return Normalizer
                .normalize(string, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    private FilterBooleanWrapper compareStringAttributes(String filterAttribute, String ressourceDiffusableValue){

        boolean stringsMatch = ressourceDiffusableValue.toLowerCase(Locale.ROOT).contains(Utils.emptyIfNull(filterAttribute));
        switch (this.operator) {
            case AND:
                return stringsMatch ? new FilterBooleanWrapper(FilterBoolean.UNKNOWN) : new FilterBooleanWrapper(FilterBoolean.FALSE);
            case OR:
                return stringsMatch ? new FilterBooleanWrapper(FilterBoolean.TRUE) : new FilterBooleanWrapper(FilterBoolean.UNKNOWN);
            default:
                return new FilterBooleanWrapper(FilterBoolean.UNKNOWN);
        }
    }

    private FilterBooleanWrapper compareBooleanAttributes(FilterBooleanWrapper filterAttribute, boolean ressourceDiffusableValue){
        FilterBoolean matchResult = filterAttribute.matchs(ressourceDiffusableValue);
        switch (this.operator) {
            case AND:
                if(matchResult == FilterBoolean.FALSE){
                    return new FilterBooleanWrapper(FilterBoolean.FALSE);
                }else {
                    return new FilterBooleanWrapper(FilterBoolean.UNKNOWN);
                }
            case OR:
                if(matchResult == FilterBoolean.FALSE || matchResult == FilterBoolean.UNKNOWN){
                    return new FilterBooleanWrapper(FilterBoolean.UNKNOWN);
                }else {
                    return new FilterBooleanWrapper(FilterBoolean.TRUE);
                }
            default:
                return new FilterBooleanWrapper(FilterBoolean.UNKNOWN);
        }
    }

    public boolean filter(RessourceDiffusable rd) {
        try {
            FilterBooleanWrapper result = compareStringAttributes(this.idRessource, rd.getRessource().getId());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            result = compareStringAttributes(this.nomRessource, rd.getRessource().getNom());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            result = compareStringAttributes(this.idEditeur, rd.getEditeur().getId());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            result = compareStringAttributes(this.nomEditeur, rd.getEditeur().getNom());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            if (this.distributeurCom != null || this.nomDistributeurCom != null) {
                for (AttributRessource dc : rd.getDistributeursCom()) {
                    result = compareStringAttributes(this.distributeurCom, dc.getId());
                    if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

                    result = compareStringAttributes(this.nomDistributeurCom, dc.getNom());
                    if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();
                }
            }

            result = compareStringAttributes(this.distributeurTech, rd.getDistributeurTech().getId());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            result = compareStringAttributes(this.nomDistributeurTech, rd.getDistributeurTech().getNom());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            result = compareBooleanAttributes(this.affichable,rd.isAffichable());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            result = compareBooleanAttributes(this.diffusable,rd.isDiffusable());
            if (result.getFilterBoolean() != FilterBoolean.UNKNOWN) return result.nonUnknownValueAsBoolean();

            switch (this.operator) {
                case AND:
                    return true;
                case OR:
                    return false;
                default:
                    return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty() {
        return this.idRessource == null &&
                this.nomRessource == null &&
                this.idEditeur == null &&
                this.nomEditeur == null &&
                this.distributeurCom == null &&
                this.distributeurTech == null &&
                this.affichable == null &&
                this.diffusable == null;
    }
}
