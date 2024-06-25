package com.example.msbankapplication.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private Long id;
    private String name;
    private String surname;
    @NotNull
    private String finNo;
    @NotNull
    private LocalDate birthDate;
}
