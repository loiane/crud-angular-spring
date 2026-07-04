package com.loiane.course.enums;

public enum Status {
    ACTIVE("Active"), INACTIVE("Inactive");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Returns the status whose display value matches the given string.
     */
    public static Status fromValue(String value) {
        for (Status status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Status: " + value);
    }

    @Override
    public String toString() {
        return value; // required for @ValueOfEnum
    }
}
