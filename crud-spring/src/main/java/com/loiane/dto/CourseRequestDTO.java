package com.loiane.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CourseRequestDTO(
        @NotBlank @NotNull @Length(min = 5, max = 200) String name,
        @NotNull @Length(max = 10) @Pattern(regexp = "back-end|front-end") String category) {
}
