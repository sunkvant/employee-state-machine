package com.sunkvant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunkvant.service.EmployeeService;
import com.sunkvant.service.TransitionService;
import com.sunkvant.statemachine.api.model.EmployeeDtoIn;
import com.sunkvant.statemachine.api.model.EmployeeDtoOut;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class EmployeeControllerTest {
    @MockBean
    private TransitionService transitionService;
    @MockBean
    private EmployeeService employeeService;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createEmployee_Success() throws Exception {
        var employeeDtoIn = new EmployeeDtoIn();
        employeeDtoIn.setName("employee");

        var employeeDtoOut = new EmployeeDtoOut();
        employeeDtoOut.setId(1L);
        employeeDtoOut.setName(employeeDtoIn.getName());
        employeeDtoOut.setState("ADDED");

        when(employeeService.createNewEmployee(any())).thenReturn(employeeDtoOut);

        var mvcResult = mockMvc.perform(post("/employee")
                        .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDtoIn)))
                .andExpect(status().isCreated()).andReturn();

        var resp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDtoOut.class);

        assertEquals( 1L,resp.getId());
        assertEquals(employeeDtoIn.getName(), resp.getName());
        assertEquals("ADDED", resp.getState());
    }

    @Test
    void createEmployee_Fail() throws Exception {
        var employeeDtoIn = new EmployeeDtoIn();
        employeeDtoIn.setName("employee");

        when(employeeService.createNewEmployee(any())).thenThrow(new RuntimeException("error"));

        mockMvc.perform(post("/employee")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDtoIn)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getEmployee_Success() throws Exception {
        var employeeDtoOut = new EmployeeDtoOut();
        employeeDtoOut.setId(1L);
        employeeDtoOut.setName("employee");
        employeeDtoOut.setState("ADDED");

        when(employeeService.getEmployee(eq(1L))).thenReturn(employeeDtoOut);

        var mvcResult = mockMvc.perform(get("/employee/{employeeId}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EmployeeDtoOut resp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDtoOut.class);

        assertEquals(employeeDtoOut.getId(), resp.getId());
        assertEquals(employeeDtoOut.getName(), resp.getName());
        assertEquals(employeeDtoOut.getState(), resp.getState());
    }

    @Test
    void getEmployee_Fail() throws Exception {
        when(employeeService.getEmployee(eq(1L))).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/employee/{employeeId}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getAllEmployees_Success() throws Exception {
        var employee = new EmployeeDtoOut();
        var employee1 = new EmployeeDtoOut();

        employee.setId(1L);
        employee.setName("employee");
        employee.setState("ADDED");

        employee.setId(2L);
        employee.setName("employee1");
        employee.setState("ADDED");

        when(employeeService.getAllEmployees()).thenReturn(List.of(employee, employee1));

        var mvcResult = mockMvc.perform(get("/employees", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        List<EmployeeDtoOut> resp = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(2, resp.size());

        assertEquals(employee.getId(), resp.get(0).getId());
        assertEquals(employee.getName(), resp.get(0).getName());
        assertEquals(employee.getState(), resp.get(0).getState());

        assertEquals(employee1.getId(), resp.get(1).getId());
        assertEquals(employee1.getName(), resp.get(1).getName());
        assertEquals(employee1.getState(), resp.get(1).getState());
    }

    @Test
    void getAllEmployees_Fail() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/employees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void transition() throws Exception {
        var employeeDtoOut = new EmployeeDtoOut();
        employeeDtoOut.setId(1L);
        employeeDtoOut.setName("employee");
        employeeDtoOut.setState("ADDED");

        when(employeeService.getEmployee(eq(1L))).thenReturn(employeeDtoOut);
        when(transitionService.transition(eq(employeeDtoOut), eq(EmployeeEvent.BEGIN_CHECK))).thenReturn(true);

        mockMvc.perform(put("/employee/{employeeId}/transition", "1")
                        .queryParam("action", EmployeeEvent.BEGIN_CHECK.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
