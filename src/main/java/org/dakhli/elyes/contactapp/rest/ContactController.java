package org.dakhli.elyes.contactapp.rest;

import org.dakhli.elyes.contactapp.common.data.PageParams;
import org.dakhli.elyes.contactapp.domain.contact.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("contacts")
public class ContactController {

    private final ContactService contactService;
    private final RandomContactsGenerator generator;

    public ContactController(ContactService contactService, RandomContactsGenerator generator) {
        this.contactService = contactService;
        this.generator = generator;
    }

    @PostMapping("add")
    public @ResponseBody Contact addContact(@RequestBody ContactInsertRequest contact) {
        return contactService.createContact(contact);
    }

    @GetMapping("all")
    public @ResponseBody List<Contact> getAllContacts(@RequestParam(name = "page", defaultValue = "1") int page,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        return contactService.findAll(new PageParams(page, pageSize));
    }

    @GetMapping("count")
    public @ResponseBody int countContacts(@RequestParam(name = "firstName", required = false) String firstName,
                                           @RequestParam(name = "lastName", required = false) String lastName,
                                           @RequestParam(name = "tel", required = false) String tel,
                                           @RequestParam(name = "email", required = false) String email) {
        ContactQuery contactQuery = new ContactQuery(Optional.ofNullable(firstName), Optional.ofNullable(lastName),
                Optional.ofNullable(tel), Optional.ofNullable(email));

        return contactQuery.isEmpty() ?
                contactService.countAll() : contactService.countByQuery(contactQuery);
    }

    @GetMapping("query")
    public @ResponseBody List<Contact> getContactsByQuery(@RequestParam(name = "firstName", required = false) String firstName,
                                                          @RequestParam(name = "lastName", required = false) String lastName,
                                                          @RequestParam(name = "tel", required = false) String tel,
                                                          @RequestParam(name = "email", required = false) String email,
                                                          @RequestParam(name = "page", defaultValue = "1") int page,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        ContactQuery contactQuery = new ContactQuery(Optional.ofNullable(firstName), Optional.ofNullable(lastName),
                Optional.ofNullable(tel), Optional.ofNullable(email));
        return contactService.findByQuery(contactQuery, new PageParams(page, pageSize));
    }

    @GetMapping("generate")
    public @ResponseBody int generateContact(@RequestParam(name = "size") int size) {
        return generator.generate(size);
    }
}
