package com.example.demo.controllers;

import com.example.demo.controller.DronesController;
import com.example.demo.dto.APIError;
import com.example.demo.dto.DroneBatteryLevelDto;
import com.example.demo.dto.DroneDto;
import com.example.demo.dto.DroneLoadingDto;
import com.example.demo.dto.DronesDto;
import com.example.demo.exceptions.TooHeavyLoadException;
import com.example.demo.exceptions.UnknownDroneException;
import com.example.demo.exceptions.UnknownMedicationException;
import com.example.demo.exceptions.UnloadableDroneException;
import com.example.demo.services.DroneService;
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
import java.util.List;
import java.util.Set;

import static com.example.demo.enums.DroneModel.CRUISER_WEIGHT;
import static com.example.demo.enums.DroneState.IDLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DronesController.class)
class DronesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DroneService droneService;

    final private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void tesRegisterDrone() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(OK.value(), result.getResponse().getStatus());
        verify(droneService).registerDrone(inputModel);

    }

    @Test
    void tesRegisterDrone_badRequest_modelIsRequired() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setModel(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("model"));

    }

    @Test
    void tesRegisterDrone_badRequest_serialIsRequired() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setSerial(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("serial"));

    }

    @Test
    void tesRegisterDrone_badRequest_emptySerial() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setSerial("");
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("serial"));

    }
    @Test
    void tesRegisterDrone_badRequest_tooLongSerial() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setSerial("""
                aaaaaaaaaaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaaaaaaaaaa""");
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("serial"));

    }

    @Test
    void tesRegisterDrone_badRequest_weightLimit_required() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setWeightLimitGrm(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("weightLimitGrm"));

    }
    @Test
    void tesRegisterDrone_badRequest_weightLimit_negative() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setWeightLimitGrm((short)-1);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("weightLimitGrm"));

    }

    @Test
    void tesRegisterDrone_badRequest_weightLimit_tooHeavy() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setWeightLimitGrm((short)501);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("weightLimitGrm"));

    }

    @Test
    void tesRegisterDrone_badRequest_batteryCapacity_required() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setBatteryCapacityPercentage(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("batteryCapacityPercentage"));

    }

    @Test
    void tesRegisterDrone_badRequest_batteryCapacity_negative() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setBatteryCapacityPercentage((byte)-1);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("batteryCapacityPercentage"));

    }

    @Test
    void tesRegisterDrone_badRequest_batteryCapacity_irrational() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setBatteryCapacityPercentage((byte)101);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("batteryCapacityPercentage"));

    }

    @Test
    void tesRegisterDrone_badRequest_state_required() throws Exception {
        final DroneDto inputModel = createTestDroneInputModel();
        inputModel.setDroneState(null);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(BAD_REQUEST.value(), result.getResponse().getStatus());
        verify(droneService,times(0)).registerDrone(inputModel);
        final APIError apiError = parseApiError(result);
        assertTrue(apiError.getDetails().containsKey("droneState"));

    }

    @Test
    void testGetAvailableDrones() throws Exception {
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/api/v1/drones/available")
                .contentType(APPLICATION_JSON);
        final DroneDto droneDto1 = mock(DroneDto.class);
        final DroneDto droneDto2 = mock(DroneDto.class);
        when(droneService.getAvailableDrones()).thenReturn(List.of(droneDto1, droneDto2));
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(OK.value(), result.getResponse().getStatus());
        verify(droneService).getAvailableDrones();
        final DronesDto actualDronesDto = objectMapper.readValue(result.getResponse().getContentAsString(), DronesDto.class);
        assertEquals(2, actualDronesDto.getDrones().size());

    }

    @Test
    void testGetDroneBatteryLevel() throws Exception {
        final long droneId = 1L;
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/api/v1/drones/"+ droneId + "/battery-level")
                .contentType(APPLICATION_JSON);
        final DroneDto droneDtoMock = mock(DroneDto.class);
        final byte batterLevel = (byte)10;
        when(droneService.getDrone(droneId)).thenReturn(droneDtoMock);
        when(droneDtoMock.getBatteryCapacityPercentage()).thenReturn(batterLevel);
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(OK.value(), result.getResponse().getStatus());
        verify(droneService).getDrone(droneId);
        final DroneBatteryLevelDto droneBatteryLevelDto = objectMapper.readValue(result.getResponse().getContentAsString(), DroneBatteryLevelDto.class);
        assertEquals(batterLevel, droneBatteryLevelDto.getBatteryLevel());

    }

    @Test
    void testGetDroneBatteryLevel_unknownDrone() throws Exception {
        final long droneId = 1L;
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/api/v1/drones/"+ droneId + "/battery-level")
                .contentType(APPLICATION_JSON);
        when(droneService.getDrone(droneId)).thenThrow(new UnknownDroneException());
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(NOT_FOUND.value(), result.getResponse().getStatus());
        verify(droneService).getDrone(droneId);

    }

    @Test
    void tesLoadDroneWithMedications() throws Exception {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medications = Set.of(medicationId1, medicationId2);
        final DroneLoadingDto inputModel = new DroneLoadingDto(medications);
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones/" + droneId + "/load-medications")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(OK.value(), result.getResponse().getStatus());
        verify(droneService).loadDroneWithMedications(droneId, medications);

    }

    @Test
    void tesLoadDroneWithMedications_unknownDrone() throws Exception {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medications = Set.of(medicationId1, medicationId2);
        final DroneLoadingDto inputModel = new DroneLoadingDto(medications);
        when(droneService.loadDroneWithMedications(droneId, medications)).thenThrow(new UnknownDroneException());
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones/" + droneId + "/load-medications")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(NOT_FOUND.value(), result.getResponse().getStatus());
        verify(droneService).loadDroneWithMedications(droneId, medications);

    }

    @Test
    void tesLoadDroneWithMedications_unknownMedication() throws Exception {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medications = Set.of(medicationId1, medicationId2);
        final DroneLoadingDto inputModel = new DroneLoadingDto(medications);
        when(droneService.loadDroneWithMedications(droneId, medications)).thenThrow(new UnknownMedicationException());
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones/" + droneId + "/load-medications")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(NOT_FOUND.value(), result.getResponse().getStatus());
        verify(droneService).loadDroneWithMedications(droneId, medications);

    }

    @Test
    void tesLoadDroneWithMedications_notLoadable() throws Exception {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medications = Set.of(medicationId1, medicationId2);
        final DroneLoadingDto inputModel = new DroneLoadingDto(medications);
        when(droneService.loadDroneWithMedications(droneId, medications)).thenThrow(new UnloadableDroneException());
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones/" + droneId + "/load-medications")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(CONFLICT.value(), result.getResponse().getStatus());
        verify(droneService).loadDroneWithMedications(droneId, medications);

    }

    @Test
    void tesLoadDroneWithMedications_tooHeavyLoad() throws Exception {
        final long droneId = 1L;
        final long medicationId1 = 2L;
        final long medicationId2 = 3L;
        final Set<Long> medications = Set.of(medicationId1, medicationId2);
        final DroneLoadingDto inputModel = new DroneLoadingDto(medications);
        when(droneService.loadDroneWithMedications(droneId, medications)).thenThrow(new TooHeavyLoadException());
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/api/v1/drones/" + droneId + "/load-medications")
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputModel))
                .contentType(APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(CONFLICT.value(), result.getResponse().getStatus());
        verify(droneService).loadDroneWithMedications(droneId, medications);

    }

    @Test
    void testGetDrone() throws Exception {
        final long droneId = 1L;
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/api/v1/drones/"+ droneId)
                .contentType(APPLICATION_JSON);
        final DroneDto droneDtoMock = mock(DroneDto.class);
        when(droneService.getDrone(droneId)).thenReturn(droneDtoMock);
        when(droneDtoMock.getId()).thenReturn(droneId);
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(OK.value(), result.getResponse().getStatus());
        verify(droneService).getDrone(droneId);
        final DroneDto droneDto = objectMapper.readValue(result.getResponse().getContentAsString(), DroneDto.class);
        assertEquals(1, droneDto.getId());
    }


    private APIError parseApiError(final MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), APIError.class);
    }

    private DroneDto createTestDroneInputModel() {
        final DroneDto inputModel = new DroneDto();
        inputModel.setModel(CRUISER_WEIGHT);
        inputModel.setDroneState(IDLE);
        inputModel.setSerial("A1");
        inputModel.setBatteryCapacityPercentage((byte)100);
        inputModel.setWeightLimitGrm((short)400);
        return inputModel;
    }
}
