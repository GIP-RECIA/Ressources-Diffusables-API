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
package fr.recia.ressourcesdiffusablesapi.service.parser.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.service.parser.IRessourceDiffusableParserService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
@Slf4j
public class RessourceDiffusableParserServiceJacksonAnnotationsImpl implements IRessourceDiffusableParserService {

    @Override
    public List<RessourceDiffusable> parseRawJsonStringIntoRessourceDiffusableList(String rawJsonString) throws IOException {

        log.info("Parsing raw JSON starting with: {}...",rawJsonString.substring(0,Math.min(25, rawJsonString.length())));
        ObjectMapper mapper = JsonMapper.builder()
                .constructorDetector(ConstructorDetector.EXPLICIT_ONLY)
                .build();
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        JsonNode rootNode = mapper.readTree(rawJsonString);

        JsonNode inside = rootNode.get(0);
        JsonNode ressourceDiffusableNode = inside.get("ressourceDiffusable");
        List<RessourceDiffusable> ressourceDiffusableList = mapper.readerForListOf(RessourceDiffusable.class).readValue(ressourceDiffusableNode);

        Set<String> propertiesKeySet = new HashSet<>();
        Consumer<RessourceDiffusable> addPropertyKeysToSet = (RessourceDiffusable rd) -> {propertiesKeySet.addAll(rd.getProperties().keySet());};

        ressourceDiffusableList.forEach(addPropertyKeysToSet);
        if(!propertiesKeySet.isEmpty()){
            String joinedKeys = String.join(" - ", propertiesKeySet);
            log.warn("Found one of more unknowns properties while parsing json file: {}",joinedKeys);
        }
        return ressourceDiffusableList;
    }
}
