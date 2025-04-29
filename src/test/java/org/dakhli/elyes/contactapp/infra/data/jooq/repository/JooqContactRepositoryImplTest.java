package org.dakhli.elyes.contactapp.infra.data.jooq.repository;

import org.dakhli.elyes.contactapp.domain.contact.Contact;
import org.dakhli.elyes.contactapp.domain.contact.ContactQuery;
import org.dakhli.elyes.contactapp.domain.contact.ContactRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dakhli.elyes.contactapp.common.data.PageParams.firstPageOfTen;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContact.aRandomContact;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContact.wiem;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContactQuery.aContactQuery;
import static org.dakhli.elyes.contactapp.infra.data.jooq.model.tables.Contact.CONTACT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JooqContactRepositoryImplTest {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    DSLContext dslContext;

    @BeforeEach
    void setUp() {
        //Clear contact table before each test
        dslContext.delete(CONTACT).execute();
    }

    @Test
    void createContact() {
        // Given
        Contact contactToSave = aRandomContact();

        // When
        Contact contact = contactRepository.createContact(contactToSave);

        // Then
        List<Contact> contactList = contactRepository.findAll(firstPageOfTen());

        assertNotNull(contact);
        assertFalse(contactList.isEmpty());
        Optional<Contact> actualContact = contactList.stream().findFirst();
        assertEquals(contactToSave, actualContact.get());
    }

    @Test
    void findAll() {
        // Given
        List<Contact> contacts = List.of(aRandomContact(), aRandomContact());
        contacts.forEach(contactRepository::createContact);

        // When
        List<Contact> actualContacts = contactRepository.findAll(firstPageOfTen());

        // Then
        assertNotNull(actualContacts);
        assertEquals(2, actualContacts.size());
        assertThat(actualContacts).containsExactlyInAnyOrderElementsOf(contacts);
    }

    @Test
    void findByQuery() {
        // Given
        assertTrue(contactRepository.findAll(firstPageOfTen()).isEmpty());
        Contact elyes = aRandomContact();
        Contact wiem = wiem();
        List<Contact> contacts = List.of(elyes, wiem);
        contacts.forEach(contactRepository::createContact);
        ContactQuery query = aContactQuery()
                .firstName("wiem")
                .toContactQuery();

        // When
        List<Contact> actualContacts = contactRepository.findByQuery(query, firstPageOfTen());

        // Then
        assertNotNull(actualContacts);
        assertFalse(actualContacts.isEmpty());
        assertIterableEquals(List.of(wiem), actualContacts);
    }
}