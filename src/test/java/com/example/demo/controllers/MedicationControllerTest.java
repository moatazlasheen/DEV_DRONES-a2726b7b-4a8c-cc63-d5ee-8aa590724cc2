package com.example.demo.controllers;

import com.example.demo.controller.MedicationController;
import com.example.demo.dto.APIError;
import com.example.demo.dto.MedicationDto;
import com.example.demo.services.MedicationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MedicationController.class)
class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicationService medicationService;

    final private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void tesRegisterMedication() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(OK.value(), result.getResponse().getStatus());
        verify(medicationService).registerMedication(inputModel);

    }

    @Test
    void tesRegisterMedication_badRequest_codeIsRequired() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setCode(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("code"));

    }

    @Test
    void tesRegisterMedication_badRequest_codeEmpty() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setCode("");
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("code"));

    }
    @Test
    void tesRegisterMedication_badRequest_codeBadPattern_dash() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setCode("ABC-DEF");
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("code"));

    }

    @Test
    void tesRegisterMedication_badRequest_codeBadPattern_lowerCaseLetters() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setCode("abc");
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("code"));

    }

    @Test
    void tesRegisterMedication_badRequest_nameIsRequired() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setName(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("name"));

    }

    @Test
    void tesRegisterMedication_badRequest_nameEmpty() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setName("");
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("name"));

    }
    @Test
    void tesRegisterMedication_badRequest_nameBadPattern() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setName("abd45-cde&");
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("name"));

    }

    @Test
    void tesRegisterMedication_badRequest_weightIsRequired() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setWeight(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("weight"));

    }

    @Test
    void tesRegisterMedication_badRequest_negativeWeight() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setWeight(-1.0f);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("weight"));

    }
    @Test
    void tesRegisterMedication_badRequest_tooHeavyForDrones() throws Exception {
        final MedicationDto inputModel = createTestMedicationInputModel();
        inputModel.setWeight(500.5f);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/medication")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(medicationService,times(0)).registerMedication(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("weight"));

    }


    private APIError parseApiError(final MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), APIError.class);
    }

    private MedicationDto createTestMedicationInputModel() {
        final MedicationDto inputModel = new MedicationDto();
        inputModel.setName("test-medication");
        inputModel.setCode("ABC_1");
        inputModel.setWeight(100.0f);
        return inputModel;
    }
}
