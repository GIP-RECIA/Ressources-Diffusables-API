package fr.recia.ressourcesdiffusablesapi.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;

import fr.recia.ressourcesdiffusablesapi.service.gar.ServiceGar;
import fr.recia.ressourcesdiffusablesapi.test.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class ApiRessourcesDiffusablesControllerTest {
    // Attributs
    @Autowired
    private ServiceGar serviceGar;

    private MockMvc mockListRessourcesMvc;

    // Méthodes
    @PostConstruct
    public void setup() {
        MockitoAnnotations.openMocks(this);

        ApiRessourcesDiffusablesController apiRessourcesDiffusablesController = new ApiRessourcesDiffusablesController();

        ReflectionTestUtils.setField(apiRessourcesDiffusablesController, "serviceGar", this.serviceGar);

        this.mockListRessourcesMvc = MockMvcBuilders.standaloneSetup(apiRessourcesDiffusablesController).build();
    }

    @Test
    public void testJsonApiRessourcesDiffusables() throws Exception {
        this.mockListRessourcesMvc.perform(get("/api/ressources-diffusables?page=0")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())

                // Status.
                .andExpect(status().isOk())

                // Content-type.
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                // Encoding.
                .andExpect(content().encoding("UTF-8"))

                // JSON Meta Analysis.
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("timestamp")))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("message")))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$", Matchers.hasKey("payloadClass")))
                .andExpect(jsonPath("$.payloadClass").isString())
                .andExpect(jsonPath("$.payloadClass").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("payload")))

                // JSON Payload Analysis.
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0]", Matchers.hasKey("ressource")))
                .andExpect(jsonPath("$.payload.[0].ressource").isMap())
                .andExpect(jsonPath("$.payload.[0].ressource").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].ressource", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.[0].ressource.id").isString())
                .andExpect(jsonPath("$.payload.[0].ressource.id").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].ressource", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.[0].ressource.nom").isString())
                .andExpect(jsonPath("$.payload.[0]", Matchers.hasKey("editeur")))
                .andExpect(jsonPath("$.payload.[0].editeur").isMap())
                .andExpect(jsonPath("$.payload.[0].editeur").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].editeur", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.[0].editeur.id").isString())
                .andExpect(jsonPath("$.payload.[0].editeur.id").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].editeur", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.[0].editeur.nom").isString())
                .andExpect(jsonPath("$.payload.[0]", Matchers.hasKey("distributeursCom")))
                .andExpect(jsonPath("$.payload.[0].distributeursCom").isArray())
                .andExpect(jsonPath("$.payload.[0].distributeursCom").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].distributeursCom.[0]").isMap())
                .andExpect(jsonPath("$.payload.[0].distributeursCom.[0]", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.[0].distributeursCom.[0].id").isString())
                .andExpect(jsonPath("$.payload.[0].distributeursCom.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].distributeursCom.[0]", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.[0].distributeursCom.[0].nom").isString())
                .andExpect(jsonPath("$.payload.[0]", Matchers.hasKey("distributeurTech")))
                .andExpect(jsonPath("$.payload.[0].distributeurTech").isMap())
                .andExpect(jsonPath("$.payload.[0].distributeurTech").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].distributeurTech", Matchers.hasKey("id")))
                .andExpect(jsonPath("$.payload.[0].distributeurTech.id").isString())
                .andExpect(jsonPath("$.payload.[0].distributeurTech.id").isNotEmpty())
                .andExpect(jsonPath("$.payload.[0].distributeurTech", Matchers.hasKey("nom")))
                .andExpect(jsonPath("$.payload.[0].distributeurTech.nom").isString())
                .andExpect(jsonPath("$.payload.[0]", Matchers.hasKey("affichable")))
                .andExpect(jsonPath("$.payload.[0].affichable").isBoolean())
                .andExpect(jsonPath("$.payload.[0]", Matchers.hasKey("diffusable")))
                .andExpect(jsonPath("$.payload.[0].diffusable").isBoolean());
    }

    @Test
    public void testJsonApiRessourcesDiffusablesBadRequest() throws Exception {
        this.mockListRessourcesMvc.perform(get("/api/ressources-diffusables?page=pokemon")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())

                // Status.
                .andExpect(status().isBadRequest())

                // Content-type.
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                // Encoding.
                .andExpect(content().encoding("UTF-8"))

                // JSON Meta Analysis.
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("timestamp")))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("message")))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$", Matchers.hasKey("payloadClass")))
                .andExpect(jsonPath("$.payloadClass").isString())
                .andExpect(jsonPath("$.payloadClass").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("payload")))

                // JSON Payload Analysis.
                .andExpect(jsonPath("$.payload").isMap())
                .andExpect(jsonPath("$.payload").isNotEmpty())
                .andExpect(jsonPath("$.payload", Matchers.hasKey("exceptionMessage")))
                .andExpect(jsonPath("$.payload.exceptionMessage").isString())
                .andExpect(jsonPath("$.payload", Matchers.hasKey("exceptionLocalizedMessage")))
                .andExpect(jsonPath("$.payload.exceptionLocalizedMessage").isString())
                .andExpect(jsonPath("$.payload", Matchers.hasKey("exceptionName")))
                .andExpect(jsonPath("$.payload.exceptionName").isString())
                .andExpect(jsonPath("$.payload.exceptionName").isNotEmpty())
        ;
    }

    @Test
    public void testJsonApiRessourcesDiffusablesPageCount() throws Exception {
        this.mockListRessourcesMvc.perform(get("/api/ressources-diffusables/page-count")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())

                // Status.
                .andExpect(status().isOk())

                // Content-type.
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                // Encoding.
                .andExpect(content().encoding("UTF-8"))

                // JSON Meta Analysis.
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("timestamp")))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("message")))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$", Matchers.hasKey("payloadClass")))
                .andExpect(jsonPath("$.payloadClass").isString())
                .andExpect(jsonPath("$.payloadClass").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("payload")))

                // JSON Analysis.
                .andExpect(jsonPath("$.payload", Matchers.notNullValue()))
                .andExpect(jsonPath("$.payload").isNumber());
    }

    @Test
    public void testJsonApiRessourcesDiffusablesSize() throws Exception {
        this.mockListRessourcesMvc.perform(get("/api/ressources-diffusables/size")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())

                // Status.
                .andExpect(status().isOk())

                // Content-type.
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))

                // Encoding.
                .andExpect(content().encoding("UTF-8"))

                // JSON Meta Analysis.
                .andExpect(jsonPath("$", Matchers.notNullValue()))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("timestamp")))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("message")))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$", Matchers.hasKey("payloadClass")))
                .andExpect(jsonPath("$.payloadClass").isString())
                .andExpect(jsonPath("$.payloadClass").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasKey("payload")))

                // JSON Analysis.
                .andExpect(jsonPath("$.payload", Matchers.notNullValue()))
                .andExpect(jsonPath("$.payload").isNumber());
    }
}
