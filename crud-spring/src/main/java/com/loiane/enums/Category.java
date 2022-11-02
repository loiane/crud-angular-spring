package com.loiane.enums;

public enum Category {
    FRONT_END("Front-end"), BACK_END("Back-end");

    private String value;

    private Category(String value) {
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
