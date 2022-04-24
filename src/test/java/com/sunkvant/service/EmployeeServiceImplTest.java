package com.sunkvant.service;

import com.sunkvant.service.EmployeeService;
import com.sunkvant.statemachine.api.model.EmployeeDtoIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
public class EmployeeServiceImplTest {

    @Autowired
    private EmployeeService employeeService;

    @BeforeEach
    void setup() {
        var emp = new EmployeeDtoIn();
        var emp1 = new EmployeeDtoIn();
        var emp2 = new EmployeeDtoIn();

        emp.setName("employee");
        emp1.setName("employee1");
        emp2.setName("employee2");

        employeeService.createNewEmployee(emp);
        employeeService.createNewEmployee(emp1);
        employeeService.createNewEmployee(emp2);
    }

    @Test
    void getEmployee_Success() {
        var employee = employeeService.getEmployee(1L);

        assertEquals("employee",employee.getName());
        assertEquals("ADDED",employee.getState());
    }

    @Test
    void getAllEmployees_Success() {
        var allEmployees = employeeService.getAllEmployees();
        assertEquals( 3, allEmployees.size());
    }

    @Test
    void getEmployee_Fail() {
        assertThrows(ResponseStatusException.class, () -> employeeService.getEmployee(100L));
    }

}
