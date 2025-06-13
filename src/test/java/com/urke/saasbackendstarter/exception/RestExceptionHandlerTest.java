package com.urke.saasbackendstarter.exception;

import com.urke.saasbackendstarter.controller.ExceptionThrowingController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ExceptionThrowingController.class, RestExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class RestExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private com.urke.saasbackendstarter.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private com.urke.saasbackendstarter.security.JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("UserNotFoundException handled with 404 and JSON body")
    void userNotFoundExceptionReturns404() throws Exception {
        given(messageSource.getMessage(eq("user.notfound"), any(), any(Locale.class)))
                .willReturn("User not found (localized)");

        mockMvc.perform(get("/test-ex/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("not_found"))
                .andExpect(jsonPath("$.message").value("User not found (localized)"));
    }

    @Test
    @DisplayName("UserAlreadyExistsException handled with 409 and JSON body")
    void userAlreadyExistsExceptionReturns409() throws Exception {
        given(messageSource.getMessage(eq("user.exists"), any(), any(Locale.class)))
                .willReturn("User already exists (localized)");

        mockMvc.perform(get("/test-ex/exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("user_exists"))
                .andExpect(jsonPath("$.message").value("User already exists (localized)"));
    }
}