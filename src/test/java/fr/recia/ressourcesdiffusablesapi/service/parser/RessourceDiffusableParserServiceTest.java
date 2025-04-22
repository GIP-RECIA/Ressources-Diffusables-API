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

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    public void missingKeysInJsonDoesNotThrows(){
        File file = new File(resourcesWithMissingKeysJsonFilePath);
        Assertions.assertDoesNotThrow(()-> parserService.parseJsonIntoRessourceDiffusableList(file));
    }


    @Test @Order(2)
    public void missingKeysInJsonAreDefaultsValuesInModel(){
        File file = new File(resourcesWithMissingKeysJsonFilePath);
        List<RessourceDiffusable> ressourceDiffusableList = parserService.parseJsonIntoRessourceDiffusableList(file);
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
    public void nullValuesInJsonDoesNotThrows(){
        File file = new File(resourcesWithNullValuesJsonFilePath);
        Assertions.assertDoesNotThrow(()-> parserService.parseJsonIntoRessourceDiffusableList(file));
    }

    @Test @Order(4)
    public void nullValuesInJsonAreDefaultsValuesInModel(){
        File file = new File(resourcesWithNullValuesJsonFilePath);
        List<RessourceDiffusable> ressourceDiffusableList = parserService.parseJsonIntoRessourceDiffusableList(file);
        for (RessourceDiffusable ressourceDiffusable : ressourceDiffusableList){
            assertNotEquals(ressourceDiffusable.getRessource(), null);
            // AttributRessource is always instantiated with empty string if no value is provided in the current implementation
            // so there is no needs to test getId() or getNom() on it
            assertNotEquals(ressourceDiffusable.getEditeur(), null);
            assertNotEquals(ressourceDiffusable.getDistributeurTech(), null);
            assertNotEquals(ressourceDiffusable.getDistributeursCom(), null);

        }
    }

    @Test @Order(5)
    public void unknownKeysInJsonDoesNotThrow()
    {
        assertDoesNotThrow( ()-> parserService.parseJsonIntoRessourceDiffusableList(new File(ressourcesWithUnknownKeys)));
    }

    @Test @Order(6)
    public void unknownKeysInJsonAreCaughtInPropertiesMap()
    {
        List<RessourceDiffusable> ressourceDiffusableList = parserService.parseJsonIntoRessourceDiffusableList(new File(ressourcesWithUnknownKeys));
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

        List<RessourceDiffusable> list = parserService.parseJsonIntoRessourceDiffusableList(new File(ressourcesWithUnknownKeys));
        List<ILoggingEvent> loggingEvents = listAppender.list;
        int count = Math.toIntExact(loggingEvents.stream()
                .filter(x -> x.getFormattedMessage().contains("Found one of more unknowns properties while parsing json file: ")).count());
        Assertions.assertTrue(count > 0);
    }






}


