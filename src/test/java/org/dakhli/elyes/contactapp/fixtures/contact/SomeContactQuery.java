package org.dakhli.elyes.contactapp.fixtures.contact;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dakhli.elyes.contactapp.domain.contact.ContactQuery;

import java.util.Optional;

@Accessors(fluent = true)
@Data
public class SomeContactQuery {
    private String firstName;
    private String lastName;
    private String tel;
    private String email;

    public static SomeContactQuery aContactQuery() {
        return new SomeContactQuery();
    }

    public ContactQuery toContactQuery() {
        return new ContactQuery(Optional.ofNullable(firstName),
                Optional.ofNullable(lastName),
                Optional.ofNullable(tel),
                Optional.ofNullable(email));
    }
}
