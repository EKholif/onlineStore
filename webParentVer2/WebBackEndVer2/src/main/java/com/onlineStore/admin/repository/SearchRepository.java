package com.onlineStore.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface SearchRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * Standardized search method for all entities.
     * Implementing repositories must provide a @Query to handle the specific
     * fields.
     */
    Page<T> findAll(String keyword, Pageable pageable);
}
