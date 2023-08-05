package ru.practicum.shareit.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.shareit.exceptions.ValidationException;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static void checkPaging(int from, int size) {
        if ((from == 0 && size == 0) || (from < 0 && size > 0) ||
                (from == 0 && size < 0)) {
            throw new ValidationException("Не верный запрос.");
        }
    }
}
