package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    Long id;
    @NotBlank(message = "Имя не должно быть пустым.")
    String name;
    @NotBlank(message = "Адрес электронной почты не должен быть пустым.")
    @Email(message = "Почта должна быть верного формата.")
    String email;
}
