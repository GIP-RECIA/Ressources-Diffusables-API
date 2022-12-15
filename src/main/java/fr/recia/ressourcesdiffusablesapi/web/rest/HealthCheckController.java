package fr.recia.ressourcesdiffusablesapi.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class HealthCheckController {

    @GetMapping(value = "/health-check")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck(HttpServletRequest request, HttpServletResponse response) {
        if(log.isDebugEnabled()) log.debug("Health check. HTTP 200: OK.");
    }
}
