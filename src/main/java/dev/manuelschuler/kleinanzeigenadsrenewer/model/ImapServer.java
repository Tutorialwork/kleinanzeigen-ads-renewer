package dev.manuelschuler.kleinanzeigenadsrenewer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ImapServer {

    private String host;
    private String username;
    private String password;

}
