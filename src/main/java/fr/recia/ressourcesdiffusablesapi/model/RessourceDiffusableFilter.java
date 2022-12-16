package fr.recia.ressourcesdiffusablesapi.model;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public class RessourceDiffusableFilter implements Serializable {

    private enum Operator { AND, OR }
    private final Operator operator;
    private final String idRessource;
    private final String nomRessource;
    private final String idEditeur;
    private final String nomEditeur;
    private final String distributeurCom;
    private final String nomDistributeurCom;
    private final String distributeurTech;
    private final String nomDistributeurTech;
    private final Boolean affichable;
    private final Boolean diffusable;

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
        this.affichable = affichable;
        this.diffusable = diffusable;
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

    public boolean isAffichable() {
        return this.affichable;
    }

    public boolean isDiffusable() {
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

    private Boolean verification(boolean assertion) {
        switch (this.operator) {
            case AND: return assertion ? null : false;
            case OR: return assertion ? true : null;
            default: return null;
        }
    }

    private boolean assertion(String argument, String attribut) {
        return unaccent(argument).toLowerCase(Locale.ROOT).contains(attribut);
    }

    public boolean filter(RessourceDiffusable rd) {
        if (this.idRessource != null) {
            Boolean verif = this.verification(this.assertion(rd.getRessource().getId(), this.idRessource));
            if (verif != null) {
                return verif;
            }
        }

        if (this.nomRessource != null) {
            Boolean verif = this.verification(this.assertion(rd.getRessource().getNom(), this.nomRessource));
            if (verif != null) {
                return verif;
            }
        }

        if (this.idEditeur != null) {
            Boolean verif = this.verification(this.assertion(rd.getEditeur().getId(), this.idEditeur));
            if (verif != null) {
                return verif;
            }
        }

        if (this.nomEditeur != null) {
            Boolean verif = this.verification(this.assertion(rd.getEditeur().getNom(), this.nomEditeur));
            if (verif != null) {
                return verif;
            }
        }

        if (this.distributeurCom != null || this.nomDistributeurCom != null) {
            for (AttributRessource distributeurCom : rd.getDistributeursCom()) {
                if (this.distributeurCom != null) {
                    Boolean verif = this.verification(this.assertion(distributeurCom.getId(), this.distributeurCom));
                    if (verif != null) {
                        return verif;
                    }
                }

                if (this.nomDistributeurCom != null) {
                    Boolean verif = this.verification(this.assertion(distributeurCom.getNom(), this.nomDistributeurCom));
                    if (verif != null) {
                        return verif;
                    }
                }
            }
        }

        if (this.distributeurTech != null) {
            Boolean verif = this.verification(this.assertion(rd.getDistributeurTech().getId(), this.distributeurTech));
            if (verif != null) {
                return verif;
            }
        }

        if (this.nomEditeur != null) {
            Boolean verif = this.verification(this.assertion(rd.getEditeur().getNom(), this.nomEditeur));
            if (verif != null) {
                return verif;
            }
        }

        if (this.affichable != null) {
            Boolean verif = this.verification(this.affichable != rd.isAffichable());
            if (verif != null) {
                return verif;
            }
        }

        if (this.diffusable != null) {
            Boolean verif = this.verification(this.diffusable != rd.isDiffusable());
            if (verif != null) {
                return verif;
            }
        }

        switch (this.operator) {
            case AND: return true;
            case OR: return false;
            default: return true;
        }
    }

    public boolean isEmpty() {
        return this.idRessource == null &&
                this.nomRessource == null &&
                this.idEditeur == null &&
                this.distributeurCom == null &&
                this.distributeurTech == null &&
                this.affichable == null &&
                this.diffusable == null;
    }
}
