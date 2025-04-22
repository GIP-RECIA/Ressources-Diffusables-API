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
package fr.recia.ressourcesdiffusablesapi.model.apiresponse;

import fr.recia.ressourcesdiffusablesapi.model.PaginationResponse;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.tuple.Tuple2Values;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ApiResponse {

    private final long timestamp;
    private final String message;
    private final Object payload;

    public ApiResponse(String message, Tuple2Values<List<RessourceDiffusable>, PaginationResponse> responseTuple2Values) {
        this.timestamp = System.currentTimeMillis();
        this.message = message;
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("ressourcesDiffusables", responseTuple2Values.getObject1());
        payloadMap.put("pagination", responseTuple2Values.getObject2());
        this.payload = payloadMap;
    }

    // used by jackson serialization
    public String getPayloadClass() {
        return this.payload.getClass().getSimpleName();
    }

}
