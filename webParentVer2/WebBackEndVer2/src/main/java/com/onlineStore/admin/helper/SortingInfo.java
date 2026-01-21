package com.onlineStore.admin.helper;

import org.springframework.data.domain.Sort;

public class SortingInfo {
    private String sortField;
    private String sortDir;

    public SortingInfo(String sortField, String sortDir) {
        this.sortField = sortField;
        this.sortDir = sortDir;
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public String getReverseSortDir() {
        return sortDir.equals("asc") ? "desc" : "asc";
    }

    public Sort getSort() {
        return sortDir.equals("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
    }
}