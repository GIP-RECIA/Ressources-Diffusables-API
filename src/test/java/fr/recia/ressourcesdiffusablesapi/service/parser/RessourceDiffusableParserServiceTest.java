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
package fr.recia.ressourcesdiffusablesapi.service.parser;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import fr.recia.ressourcesdiffusablesapi.config.AppProperties;
import fr.recia.ressourcesdiffusablesapi.config.ParserConfiguration;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.service.parser.impl.RessourceDiffusableParserServiceJacksonAnnotationsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class RessourceDiffusableParserServiceTest {

    protected final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ParserConfiguration.class))
            .withConfiguration(AutoConfigurations.of(AppProperties.class));

    private final String resourcesWithMissingKeysJsonFilePath =
            "src/test/resources/parserJsonFiles/resources-with-missing-values.json";

    private final String resourcesWithNullValuesJsonFilePath =
            "src/test/resources/parserJsonFiles/resources-with-null-values.json";
    private final String ressourcesWithUnknownKeys =
            "src/test/resources/parserJsonFiles/resources-with-unknown-keys.json";


    private ListAppender<ILoggingEvent> listAppender;

    protected   IRessourceDiffusableParserService parserService;


    abstract void setParserServiceFromBean();


    @BeforeEach
    void init() {
        final var log = (Logger) LoggerFactory.getLogger(RessourceDiffusableParserServiceJacksonAnnotationsImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);
    }

    @Test @Order(1)
    public void missingKeysInJsonDoesNotThrows() throws IOException {

        String content = Files.readString(Path.of(resourcesWithMissingKeysJsonFilePath), Charset.defaultCharset());
        Assertions.assertDoesNotThrow(()-> parserService.parseRawJsonStringIntoRessourceDiffusableList(content));
    }


    @Test @Order(2)
    public void missingKeysInJsonAreDefaultsValuesInModel() throws IOException {
        String content = Files.readString(Path.of(resourcesWithMissingKeysJsonFilePath), Charset.defaultCharset());
        List<RessourceDiffusable> ressourceDiffusableList = parserService.parseRawJsonStringIntoRessourceDiffusableList(content);
        for (RessourceDiffusable ressourceDiffusable : ressourceDiffusableList){
            assertNotEquals(null, ressourceDiffusable.getRessource());
            assertNotEquals(null, ressourceDiffusable.getRessource().getId());
            assertNotEquals(null, ressourceDiffusable.getRessource().getId());
            assertNotEquals(null, ressourceDiffusable.getEditeur());
            assertNotEquals(null, ressourceDiffusable.getDistributeurTech());
            assertNotEquals(null, ressourceDiffusable.getDistributeursCom());
        }
    }

    @Test @Order(3)
    public void nullValuesInJsonDoesNotThrows() throws IOException {
        String content = Files.readString(Path.of(resourcesWithNullValuesJsonFilePath), Charset.defaultCharset());
        Assertions.assertDoesNotThrow(()-> parserService.parseRawJsonStringIntoRessourceDiffusableList(content));
    }

    @Test @Order(4)
    public void nullValuesInJsonAreDefaultsValuesInModel() throws IOException {
        String content = Files.readString(Path.of(resourcesWithNullValuesJsonFilePath), Charset.defaultCharset());
        List<RessourceDiffusable> ressourceDiffusableList = parserService.parseRawJsonStringIntoRessourceDiffusableList(content);
        for (RessourceDiffusable ressourceDiffusable : ressourceDiffusableList){
            assertNotEquals(null, ressourceDiffusable.getRessource());
            // AttributRessource is always instantiated with empty string if no value is provided in the current implementation
            // so there is no needs to test getId() or getNom() on it
            assertNotEquals(null, ressourceDiffusable.getEditeur());
            assertNotEquals(null, ressourceDiffusable.getDistributeurTech());
            assertNotEquals(null, ressourceDiffusable.getDistributeursCom());

        }
    }

    @Test @Order(5)
    public void unknownKeysInJsonDoesNotThrow() throws IOException {
        String content = Files.readString(Path.of(ressourcesWithUnknownKeys), Charset.defaultCharset());
        assertDoesNotThrow( ()-> parserService.parseRawJsonStringIntoRessourceDiffusableList(content));
    }

    @Test @Order(6)
    public void unknownKeysInJsonAreCaughtInPropertiesMap() throws IOException {
        String content = Files.readString(Path.of(ressourcesWithUnknownKeys), Charset.defaultCharset());
        List<RessourceDiffusable> ressourceDiffusableList = parserService.parseRawJsonStringIntoRessourceDiffusableList(content);
        boolean atLeastOnePropertiesMapNotEmpty = false;
        for(RessourceDiffusable ressourceDiffusable: ressourceDiffusableList){
            if(!ressourceDiffusable.getProperties().isEmpty()){
                atLeastOnePropertiesMapNotEmpty = true;
                break;
            }
        }
        assertTrue(atLeastOnePropertiesMapNotEmpty,"All properties map where empty");
    }

    @Test @Order(7)
    public void unknownKeysInJsonLogged_OK() throws IOException {
        String content = Files.readString(Path.of(ressourcesWithUnknownKeys), Charset.defaultCharset());
        List<RessourceDiffusable> list = parserService.parseRawJsonStringIntoRessourceDiffusableList(content);
        List<ILoggingEvent> loggingEvents = listAppender.list;
        int count = Math.toIntExact(loggingEvents.stream()
                .filter(x -> x.getFormattedMessage().contains("Found one of more unknowns properties while parsing json file: ")).count());
        Assertions.assertTrue(count > 0);
    }
}