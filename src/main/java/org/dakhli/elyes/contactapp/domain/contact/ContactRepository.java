package org.dakhli.elyes.contactapp.domain.contact;

import org.dakhli.elyes.contactapp.common.data.PageParams;

import java.util.List;

public interface ContactRepository {
    Contact createContact(Contact contact);

    List<Contact> findAll(PageParams pageParams);

    List<Contact> findByQuery(ContactQuery contactQuery, PageParams pageParams);

    int countAll();

    int countByQuery(ContactQuery contactQuery);
}
