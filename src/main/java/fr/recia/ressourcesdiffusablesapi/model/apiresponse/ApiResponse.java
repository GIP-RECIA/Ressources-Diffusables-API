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

public class ApiResponse {

    private final long timestamp;
    private String message;
    private Object payload;

    private ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(String message, Object payload) {
        this();
        this.message = message;
        this.payload = payload;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPayloadClass() {
        return this.payload.getClass().getSimpleName();
    }

    public Object getPayload() {
        return this.payload;
    }

}
