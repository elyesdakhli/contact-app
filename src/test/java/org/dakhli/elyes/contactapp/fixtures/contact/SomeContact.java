package org.dakhli.elyes.contactapp.fixtures.contact;

import com.github.javafaker.Faker;
import de.huxhorn.sulky.ulid.ULID;
import org.dakhli.elyes.contactapp.domain.contact.Contact;
import org.dakhli.elyes.contactapp.domain.contact.ContactInsertRequest;

public class SomeContact {
    private static final ULID ulid = new ULID();

    public static Contact aContact() {
        return elyes();
    }

    public static Contact elyes() {
        return new Contact(ulid.nextULID(),
                "elyes",
                "dakhli",
                "0611223344",
                "elyes.elyes@gmail.com");
    }

    public static Contact wiem() {
        return new Contact(ulid.nextULID(),
                "wiem",
                "boudaya",
                "0644112233",
                "wiem.wiem@gmail.com");
    }

    public static Contact aRandomContact() {
        Faker faker = new Faker();
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var tel = faker.phoneNumber().phoneNumber();
        var email = faker.internet().emailAddress();
        return new Contact(ulid.nextULID(), firstName, lastName, tel, email);
    }


    public static Contact fromContactInsertRequest(ContactInsertRequest contact) {
        return new Contact(ulid.nextULID(),
                contact.firstName(), contact.lastName(), contact.tel(), contact.email());
    }
}
