package org.dakhli.elyes.contactapp.domain.contact;

import org.dakhli.elyes.contactapp.common.data.PageParams;

import java.util.List;

public interface ContactService {

    Contact createContact(ContactInsertRequest newContact);

    List<Contact> findAll();

    List<Contact> findAll(PageParams pageParams);

    List<Contact> findByQuery(ContactQuery contactQuery, PageParams pageParams);

    int countAll();

    int countByQuery(ContactQuery contactQuery);
}
