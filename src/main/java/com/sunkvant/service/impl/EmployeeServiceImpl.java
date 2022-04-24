package com.sunkvant.service.impl;

import com.sunkvant.entity.Employee;
import com.sunkvant.repository.EmployeeRepository;
import com.sunkvant.service.EmployeeService;
import com.sunkvant.statemachine.api.model.EmployeeDtoIn;
import com.sunkvant.statemachine.api.model.EmployeeDtoOut;
import com.sunkvant.statemachine.api.model.EmployeeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDtoOut createNewEmployee(EmployeeDtoIn employeeDtoIn) {
        var employee = convertDtoToEntity(employeeDtoIn);
        employeeRepository.save(employee);
        log.info(String.format("Employee created: %s", employee));
        return convertEntityToDto(employee);
    }

    @Override
    public EmployeeDtoOut getEmployee(Long employeeId) {
        var optionalEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Employee is not exist: [id=%s]", employeeId)));
        return convertEntityToDto(optionalEmployee);
    }

    @Override
    public List<EmployeeDtoOut> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private EmployeeDtoOut convertEntityToDto(Employee employee) {
        var out = new EmployeeDtoOut();

        out.setId(employee.getId());
        out.setState(employee.getState());
        out.setName(employee.getName());

        return out;
    }

    private Employee convertDtoToEntity(EmployeeDtoIn employeeDtoIn) {
        var employee = new Employee();

        employee.setState(EmployeeState.ADDED.name());
        employee.setName(employeeDtoIn.getName());

        return employee;
    }
}
