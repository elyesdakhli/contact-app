package org.dakhli.elyes.contactapp.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.dakhli.elyes.contactapp.common.data.PageParams;
import org.dakhli.elyes.contactapp.domain.contact.Contact;
import org.dakhli.elyes.contactapp.domain.contact.ContactInsertRequest;
import org.dakhli.elyes.contactapp.domain.contact.ContactQuery;
import org.dakhli.elyes.contactapp.domain.contact.ContactService;
import org.dakhli.elyes.contactapp.utils.ContactEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.dakhli.elyes.contactapp.common.data.PageParams.firstPageOfTen;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContact.aRandomContact;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContact.fromContactInsertRequest;
import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContactInsertRequest.aContactInsertRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @MockitoBean
    private ContactService contactService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void addContact() {
        // Given
        ContactInsertRequest contact = aContactInsertRequest();
        Contact contactWithId = fromContactInsertRequest(contact);
        Mockito.when(contactService.createContact(ArgumentMatchers.eq(contact))).thenReturn(contactWithId);

        // When // Then
        mockMvc.perform(post(ContactEndpoint.ADD.getFullPath())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(contact)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(contactWithId)));
    }

    @Test
    @SneakyThrows
    void getAllContacts() {
        // Given
        List<Contact> contacts = List.of(aRandomContact(), aRandomContact());
        Mockito.when(contactService.findAll(ArgumentMatchers.any(PageParams.class))).thenReturn(contacts);

        // When // Then
        mockMvc.perform(get(ContactEndpoint.ALL.getFullPath()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(contacts)));
    }

    @Test
    @SneakyThrows
    void getContactsByQuery() {
        // Given
        Contact aRandomContact = aRandomContact();
        List<Contact> contacts = List.of(aRandomContact);
        Mockito.when(contactService.findByQuery(ArgumentMatchers.any(ContactQuery.class), ArgumentMatchers.eq(firstPageOfTen()))).thenReturn(contacts);

        // When // Then
        mockMvc.perform(get(ContactEndpoint.BY_QUERY.getFullPath("firstName={firstName}"), aRandomContact.firstName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(contacts)));
    }
}
