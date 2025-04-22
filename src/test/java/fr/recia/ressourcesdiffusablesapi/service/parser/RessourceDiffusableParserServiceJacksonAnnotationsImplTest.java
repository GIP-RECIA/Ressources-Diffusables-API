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

import fr.recia.ressourcesdiffusablesapi.config.ParserConfiguration;
import org.junit.jupiter.api.BeforeAll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RessourceDiffusableParserServiceJacksonAnnotationsImplTest extends RessourceDiffusableParserServiceTest {


    @BeforeAll
    @Override
    void setParserServiceFromBean() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).hasSingleBean(IRessourceDiffusableParserService.class);
                    assertEquals(context.getBean(IRessourceDiffusableParserService.class),
                            context.getBean(ParserConfiguration.class).jsonParser(),"Loaded parser bean is not RessourceDiffusableAnnotationsBasedJsonParserServiceImpl");
                    parserService = context.getBean(IRessourceDiffusableParserService.class);
                });
    }
}
