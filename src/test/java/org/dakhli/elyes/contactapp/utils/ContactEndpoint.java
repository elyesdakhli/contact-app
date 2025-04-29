package org.dakhli.elyes.contactapp.utils;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ContactEndpoint {

    BASE_PATH("/contacts"),
    ADD("/add"),
    ALL("/all"),
    BY_QUERY("/query");

    private final String path;

    ContactEndpoint(String path) {
        this.path = path;
    }

    public String getFullPath() {
        return this == BASE_PATH ? path : BASE_PATH.path + path;
    }

    public String getFullPath(String... params) {
        if (params.length == 0) {
            return getFullPath();
        }

        return getFullPath() + "?" + String.join("&", Arrays.asList(params));
    }

}
