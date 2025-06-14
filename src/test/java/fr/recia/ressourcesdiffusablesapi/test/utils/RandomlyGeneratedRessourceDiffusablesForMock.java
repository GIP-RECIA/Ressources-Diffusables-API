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
import java.util.UUID;

public class RandomlyGeneratedRessourceDiffusablesForMock {

    public  static List<RessourceDiffusable> get(int count){
        List<RessourceDiffusable> ressourceDiffusables = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            RessourceDiffusable rd = new RessourceDiffusable(UUID.randomUUID().toString(), UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),List.of(new AttributRessource(UUID.randomUUID().toString(),UUID.randomUUID().toString())), false, false, false  );
            ressourceDiffusables.add(rd);
        }
        return ressourceDiffusables;
    }

}
