/*
 * This file is generated by jOOQ.
 */
package org.dakhli.elyes.contactapp.infra.data.jooq.model;


import java.util.Arrays;
import java.util.List;

import org.dakhli.elyes.contactapp.infra.data.jooq.model.tables.Contact;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>PUBLIC</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>PUBLIC.CONTACT</code>.
     */
    public final Contact CONTACT = Contact.CONTACT;

    /**
     * No further instances allowed
     */
    private Public() {
        super("PUBLIC", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Contact.CONTACT
        );
    }
}
