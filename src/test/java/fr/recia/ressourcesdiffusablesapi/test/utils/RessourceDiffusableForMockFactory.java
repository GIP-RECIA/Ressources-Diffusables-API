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
package fr.recia.ressourcesdiffusablesapi.test.utils;

import fr.recia.ressourcesdiffusablesapi.model.AttributRessource;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;

import java.util.ArrayList;
import java.util.List;

public class RessourceDiffusableForMockFactory {


    public static List<RessourceDiffusable> argAsAllValues(String stringArg, boolean booleanArg, int count){
        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            AttributRessource attributRessource = new AttributRessource(String.format("%s_id_%d",stringArg, i),String.format("%s_nom_%d",stringArg, i));
            ressourceDiffusableList.add(new RessourceDiffusable(attributRessource.copy(),attributRessource.copy(),List.of(attributRessource.copy()),attributRessource.copy(), booleanArg,booleanArg,booleanArg,""));
        }
        return ressourceDiffusableList;
    }

    public static List<RessourceDiffusable> argsRessourceAndEditeur(String ressource, String editeur, boolean booleanArg, int count){
        List<RessourceDiffusable> ressourceDiffusableList = new ArrayList<>(count);


        for (int i = 0; i < count; i++) {
            AttributRessource attributRessourceRessource = new AttributRessource(String.format("%s_id_%d",ressource,i),String.format("%s_nom_%d",ressource,i));
            AttributRessource attributRessourceEditeur = new AttributRessource(String.format("%s_id_%d",editeur,i),String.format("%s_nom_%d",editeur,i));

            AttributRessource attributRessourceOther = new AttributRessource(String.format("%s_id_%d","other",i),String.format("%s_nom_%d","other",i));

            ressourceDiffusableList.add(new RessourceDiffusable(attributRessourceRessource.copy(),attributRessourceEditeur.copy(),List.of(attributRessourceOther.copy()),attributRessourceOther.copy(), booleanArg,booleanArg,booleanArg,""));
        }
        return ressourceDiffusableList;
    }


}
