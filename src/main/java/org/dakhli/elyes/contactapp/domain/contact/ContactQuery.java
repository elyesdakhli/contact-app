package org.dakhli.elyes.contactapp.domain.contact;

import java.util.Optional;

public record ContactQuery(
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<String> tel,
        Optional<String> email
) {

    public boolean isEmpty() {
        return firstName.isPresent() && lastName.isPresent() && tel.isPresent() && email.isPresent();
    }
}
