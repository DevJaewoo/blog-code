package com.devjaewoo.springoauth2test.domain.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class SessionClient implements Serializable {
    private String name;
    private String email;

    public SessionClient(Client client) {
        this.name = client.getName();
        this.email = client.getEmail();
    }
}
