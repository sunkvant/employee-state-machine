package com.sunkvant.service;

import com.sunkvant.statemachine.api.model.EmployeeDtoIn;
import com.sunkvant.statemachine.api.model.EmployeeDtoOut;

import java.util.List;

public interface EmployeeService {
    EmployeeDtoOut createNewEmployee(EmployeeDtoIn employeeDtoIn);
    EmployeeDtoOut getEmployee(Long employeeId);
    List<EmployeeDtoOut> getAllEmployees();
}
