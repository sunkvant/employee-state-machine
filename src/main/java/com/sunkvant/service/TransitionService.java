package com.sunkvant.service;

import com.sunkvant.entity.Employee;
import com.sunkvant.statemachine.api.model.EmployeeDtoIn;
import com.sunkvant.statemachine.api.model.EmployeeDtoOut;
import com.sunkvant.statemachine.api.model.EmployeeEvent;

public interface TransitionService {
    boolean transition(EmployeeDtoOut employee, EmployeeEvent event);
}
