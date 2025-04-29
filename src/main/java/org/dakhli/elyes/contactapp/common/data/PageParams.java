package org.dakhli.elyes.contactapp.common.data;

public record PageParams(int pageIndex, int pageSize) {

    public static PageParams firstPageOfTen() {
        return new PageParams(1, 10);
    }
}
