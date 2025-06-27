package com.onlineStore.admin.helper;

public class PaginationInfo {
    private final int currentPage;
    private final int pageSize;
    private final long totalElements;

    public PaginationInfo(int currentPage, int pageSize, long totalElements) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public long getStartCount() {
        return (long) (currentPage - 1) * pageSize + 1;
    }

    public long getEndCount() {
        long endCount = getStartCount() + pageSize - 1;
        return Math.min(endCount, totalElements);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / pageSize);
    }
}