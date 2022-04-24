package com.sunkvant.controller;

import com.sunkvant.service.EmployeeService;
import com.sunkvant.service.TransitionService;
import com.sunkvant.statemachine.api.EmployeeControllerApi;
import com.sunkvant.statemachine.api.model.EmployeeDtoIn;
import com.sunkvant.statemachine.api.model.EmployeeDtoOut;
import com.sunkvant.statemachine.api.model.EmployeeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmployeeController implements EmployeeControllerApi {

    private final TransitionService transitionService;
    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<EmployeeDtoOut> addNewEmployee(EmployeeDtoIn employeeDtoIn) {
        try {
            var employee = employeeService.createNewEmployee(employeeDtoIn);
            return new ResponseEntity<>(employee, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(String.format("Cannot save employee [%s]: %s", employeeDtoIn.toString(), e.getMessage()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }



    @Override
    public ResponseEntity<Void> transition(Long employeeId, EmployeeEvent action) {
        try {
            var employee = employeeService.getEmployee(employeeId);
            boolean accepted = transitionService.transition(employee, action);
            if (!accepted) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transition is not allowed in current state");
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(String.format("Cannot execute action %s: %s", action, e.getMessage()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<EmployeeDtoOut> getEmployee(Long employeeId) {
        try {
            var employee = employeeService.getEmployee(employeeId);
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(String.format("Cannot get employee by id [%s]: %s", employeeId, e.getMessage()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @Override
    public ResponseEntity<List<EmployeeDtoOut>> getAllEmployees() {
        try {
            var employees = employeeService.getAllEmployees();
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("Cannot get employees: %s", e.getMessage()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
