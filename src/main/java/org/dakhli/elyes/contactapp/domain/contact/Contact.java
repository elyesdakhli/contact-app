package org.dakhli.elyes.contactapp.domain.contact;

import java.util.Objects;

public record Contact(
        String id,
        String firstName,
        String lastName,
        String tel,
        String email
) {
    public Contact{
        Objects.requireNonNull(id);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(tel);
        Objects.requireNonNull(email);
    }
}
