package com.loiane.course.enums;

public enum Category {
    FRONT_END("Front-end"), BACK_END("Back-end");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value; // required for @ValueOfEnum
    }

}
