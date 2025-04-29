package org.dakhli.elyes.contactapp.domain.contact.impl;

import de.huxhorn.sulky.ulid.ULID;
import org.dakhli.elyes.contactapp.common.data.PageParams;
import org.dakhli.elyes.contactapp.domain.contact.Contact;
import org.dakhli.elyes.contactapp.domain.contact.ContactInsertRequest;
import org.dakhli.elyes.contactapp.domain.contact.ContactQuery;
import org.dakhli.elyes.contactapp.domain.contact.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.dakhli.elyes.contactapp.common.data.PageParams.firstPageOfTen;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContact.elyes;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContact.wiem;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContactInsertRequest.aContactInsertRequest;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContactQuery.aContactQuery;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @Test
    void createContact() {
        // Given
        ContactInsertRequest cir = aContactInsertRequest();
        Mockito.when(contactRepository.createContact(ArgumentMatchers.any(Contact.class)))
                .thenReturn(new Contact(new ULID().nextULID(), cir.firstName(), cir.lastName(), cir.tel(), cir.email()));

        // When
        Contact contact = contactService.createContact(cir);

        // Then
        assertNotNull(contact);
        assertNotNull(contact.id());
    }

    @Test
    void findAll() {
        // Given
        Contact elyes = elyes();
        Contact wiem = wiem();
        List<Contact> expectedContacts = List.of(elyes, wiem);

        Mockito.when(contactRepository.findAll(ArgumentMatchers.any(PageParams.class)))
                .thenReturn(expectedContacts);

        // When
        List<Contact> actualContacts = contactService.findAll();

        // Then
        assertNotNull(actualContacts);
        assertIterableEquals(expectedContacts, actualContacts);

    }

    @Test
    void findByQuery() {
        // Given
        Contact elyes = elyes();
        List<Contact> expectedContacts = List.of(elyes);

        Mockito.when(contactRepository.findByQuery(ArgumentMatchers.any(ContactQuery.class), ArgumentMatchers.eq(firstPageOfTen())))
                .thenReturn(expectedContacts);
        ContactQuery query = aContactQuery()
                .firstName("elyes")
                .toContactQuery();
        // When
        List<Contact> actualContacts = contactService.findByQuery(query, firstPageOfTen());

        // Then
        assertNotNull(actualContacts);
        assertIterableEquals(expectedContacts, actualContacts);
        assertEquals("elyes", actualContacts.get(0).firstName());
    }
}