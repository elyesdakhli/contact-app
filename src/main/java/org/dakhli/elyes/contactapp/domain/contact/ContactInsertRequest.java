package org.dakhli.elyes.contactapp.domain.contact;

public record ContactInsertRequest(
        String firstName,
        String lastName,
        String tel,
        String email
) {
}
