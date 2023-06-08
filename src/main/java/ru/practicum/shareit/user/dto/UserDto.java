package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    Long id;
    @NotBlank(message = "Имя не должно быть пустым.")
    String name;
    @NotBlank(message = "Адрес электронной почты не должен быть пустым.")
    @Email(message = "Почта должна быть верного формата.")
    String email;
}
