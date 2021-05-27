package fr.anael.apimediacentre.web.rest;

import fr.anael.apimediacentre.test.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class HealthCheckTest {
    // Attributs.
    private MockMvc mockHealthCheckMvc;

    // Méthodes
    @PostConstruct
    public void setup() {
        MockitoAnnotations.openMocks(this);

        HealthCheckController healthCheckController = new HealthCheckController();

        this.mockHealthCheckMvc = MockMvcBuilders.standaloneSetup(healthCheckController).build();
    }

    @Test
    public void testHealthCheck() throws Exception {
        mockHealthCheckMvc.perform(head("/health-check")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
