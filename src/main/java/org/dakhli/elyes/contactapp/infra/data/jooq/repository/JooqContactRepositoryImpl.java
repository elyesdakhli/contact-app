package org.dakhli.elyes.contactapp.infra.data.jooq.repository;

import org.dakhli.elyes.contactapp.common.data.PageParams;
import org.dakhli.elyes.contactapp.domain.contact.Contact;
import org.dakhli.elyes.contactapp.domain.contact.ContactQuery;
import org.dakhli.elyes.contactapp.domain.contact.ContactRepository;
import org.dakhli.elyes.contactapp.infra.data.jooq.model.tables.records.ContactRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.dakhli.elyes.contactapp.infra.data.jooq.model.tables.Contact.CONTACT;

@Repository
public class JooqContactRepositoryImpl implements ContactRepository {
    private static final TableField<ContactRecord, String>[] FIELDS =
            new TableField[]{
                    CONTACT.ID,
                    CONTACT.FIRSTNAME,
                    CONTACT.LASTNAME,
                    CONTACT.TEL,
                    CONTACT.EMAIL
            };
    private final DSLContext dslContext;


    public JooqContactRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Contact createContact(Contact contact) {
        dslContext.insertInto(CONTACT)
                .columns(FIELDS)
                .values(contact.id(), contact.firstName(), contact.lastName(), contact.tel(), contact.email())
                .execute();
        return contact;
    }

    @Override
    public List<Contact> findAll(PageParams pageParams) {
        return dslContext.select(FIELDS)
                .from(CONTACT)
                .orderBy(CONTACT.FIRSTNAME.asc())
                .limit(pageParams.pageSize())
                .offset((pageParams.pageIndex() - 1) * pageParams.pageSize())
                .fetchInto(Contact.class);
    }

    @Override
    public List<Contact> findByQuery(ContactQuery contactQuery, PageParams pageParams) {
        List<Condition> conditions = fromContactQueryToConditions(contactQuery);
        SelectJoinStep<Record> selectClause = dslContext.select(FIELDS)
                .from(CONTACT);
        if (!conditions.isEmpty()) {
            return selectClause.where(conditions.toArray(new Condition[conditions.size()]))
                    .orderBy(CONTACT.FIRSTNAME.asc())
                    .limit(pageParams.pageSize())
                    .offset((pageParams.pageIndex() - 1) * pageParams.pageSize())
                    .fetchInto(Contact.class);
        }
        return selectClause.fetchInto(Contact.class);
    }

    private List<Condition> fromContactQueryToConditions(ContactQuery contactQuery) {
        List<Condition> conditions = new ArrayList<>();
        contactQuery.firstName().ifPresent(val -> conditions.add(CONTACT.FIRSTNAME.likeIgnoreCase(val + "%")));
        contactQuery.lastName().ifPresent(val -> conditions.add(CONTACT.LASTNAME.likeIgnoreCase(val + "%")));
        contactQuery.tel().ifPresent(val -> conditions.add(CONTACT.TEL.likeIgnoreCase(val + "%")));
        contactQuery.email().ifPresent(val -> conditions.add(CONTACT.EMAIL.likeIgnoreCase(val + "%")));
        return conditions;
    }

    @Override
    public int countAll() {
        return dslContext.fetchCount(DSL.selectFrom(CONTACT));
    }

    @Override
    public int countByQuery(ContactQuery contactQuery) {
        List<Condition> conditions = fromContactQueryToConditions(contactQuery);
        
        return dslContext.fetchCount(DSL.selectFrom(CONTACT).where(conditions.toArray(new Condition[conditions.size()])));
    }


}
