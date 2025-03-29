package dev.manuelschuler.kleinanzeigenadsrenewer.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RenewException extends Throwable {

    private String message;

}
