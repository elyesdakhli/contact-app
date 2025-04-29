package org.dakhli.elyes.contactapp.fixtures.contact;

import com.github.javafaker.Faker;
import org.dakhli.elyes.contactapp.domain.contact.ContactInsertRequest;

public class SomeContactInsertRequest {
    private static final Faker faker = new Faker();

    public static ContactInsertRequest aContactInsertRequest() {
        return aRandomContactInsertRequest();
    }

    public static ContactInsertRequest elyes() {
        return new ContactInsertRequest("Elyes",
                "Dakhli",
                "0619918960",
                "dakhli.elyes@gmail.com");
    }

    public static ContactInsertRequest wiem() {
        return new ContactInsertRequest("Wiem",
                "boudaya",
                "0619918960",
                "wiem.boudaya@gmail.com");
    }
    
    public static ContactInsertRequest aRandomContactInsertRequest() {
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var tel = faker.phoneNumber().phoneNumber();
        var email = faker.internet().emailAddress();
        return new ContactInsertRequest(firstName, lastName, tel, email);
    }
}
