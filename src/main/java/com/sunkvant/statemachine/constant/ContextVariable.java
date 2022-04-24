package com.sunkvant.statemachine.constant;

public enum ContextVariable {
    EMPLOYEE_ID("EMPLOYEE_ID");

    private final String name;

    ContextVariable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
