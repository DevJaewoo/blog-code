package com.devjaewoo.springoauth2test.domain.client;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    MANAGER("ROLE_MANAGER"),
    CLIENT("ROLE_CLIENT"),
    GUEST("ROLE_GUEST");

    public final String key;
}
