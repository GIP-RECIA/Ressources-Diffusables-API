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

package fr.recia.ressourcesdiffusablesapi.model.jsonmirror;


import fr.recia.ressourcesdiffusablesapi.model.AttributRessource;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class RessourceDiffusableJsonMirror {

    @Getter @Setter
    String idRessource;
    @Getter @Setter
    String nomRessource;
    @Getter @Setter
    String idEditeur;
    @Getter @Setter
    String nomEditeur;
    @Getter @Setter
    String distributeurTech;
    @Getter @Setter
    String nomDistributeurTech;
    @Getter @Setter
    boolean affichable;
    @Getter @Setter
    boolean diffusable;
    @Getter @Setter
    boolean mereFamille;
    @Getter @Setter
    String membreFamille;
    @Getter @Setter
    DistributeurComJsonMirror[] distributeursCom;

    private void replaceNullValuesByDefaults(){
        idRessource = idRessource == null ? "" : idRessource;
        nomRessource = nomRessource == null ? "" : nomRessource;
        idEditeur = idEditeur == null ? "" : idEditeur;
        nomEditeur = nomEditeur == null ? "" : nomEditeur;
        distributeurTech = distributeurTech == null ? "" : distributeurTech;
        nomDistributeurTech = nomDistributeurTech == null ? "" : nomDistributeurTech;
        membreFamille = membreFamille == null ? "" : membreFamille;
        if(distributeursCom != null){
            for (DistributeurComJsonMirror distributeurComJsonMirror : distributeursCom) {
                distributeurComJsonMirror.replaceNullValuesByDefaults();
            }
        }else{
            distributeursCom = new DistributeurComJsonMirror[1];
            distributeursCom[0] = new DistributeurComJsonMirror();
            distributeursCom[0].setDistributeurCom("");
            distributeursCom[0].setNomDistributeurCom("");
        }

    }

    RessourceDiffusable toRessourceDiffusable(){

        replaceNullValuesByDefaults();

        AttributRessource ressource = new AttributRessource(this.idRessource, this.nomRessource);
        AttributRessource editeur = new AttributRessource(this.idEditeur,this.nomEditeur);
        AttributRessource disTech = new AttributRessource(this.distributeurTech, this.nomDistributeurTech);
        List<AttributRessource> disCom = new ArrayList<>();
        for (DistributeurComJsonMirror distributeurComJsonMirror : distributeursCom) {
            disCom.add(new AttributRessource(distributeurComJsonMirror.distributeurCom, distributeurComJsonMirror.nomDistributeurCom));
        }
        return new RessourceDiffusable( ressource, editeur, disCom, disTech, affichable, diffusable, mereFamille, membreFamille);
    }


}
