package org.dakhli.elyes.contactapp.domain.contact.impl;

import de.huxhorn.sulky.ulid.ULID;
import org.dakhli.elyes.contactapp.common.data.PageParams;
import org.dakhli.elyes.contactapp.domain.contact.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact createContact(ContactInsertRequest newContact) {
        Contact contactToCreate = new Contact(new ULID().nextULID(),
                newContact.firstName(),
                newContact.lastName(),
                newContact.tel(),
                newContact.email());
        return contactRepository.createContact(contactToCreate);
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAll(new PageParams(1, 10));
    }

    @Override
    public List<Contact> findAll(PageParams pageParams) {
        return contactRepository.findAll(pageParams);
    }

    @Override
    public List<Contact> findByQuery(ContactQuery contactQuery, PageParams pageParams) {
        return contactRepository.findByQuery(contactQuery, pageParams);
    }

    @Override
    public int countAll() {
        return contactRepository.countAll();
    }

    @Override
    public int countByQuery(ContactQuery contactQuery) {
        return contactRepository.countByQuery(contactQuery);
    }


}
