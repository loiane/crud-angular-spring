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

    /**
     * Returns the category whose display value matches the given string.
     */
    public static Category fromValue(String value) {
        for (Category category : values()) {
            if (category.value.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid Category: " + value);
    }

    @Override
    public String toString() {
        return value; // required for @ValueOfEnum
    }

}
