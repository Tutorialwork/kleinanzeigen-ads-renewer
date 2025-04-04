package dev.manuelschuler.kleinanzeigenadsrenewer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserAgent {

    private String ua;
    private Double pct;

}
