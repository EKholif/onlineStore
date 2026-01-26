package com.onlineStore.admin.repository.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface SearchableRepository<T, ID> extends Repository<T, ID> {

    // Contract methods for search. 
    // Note: @Query must be defined in the concrete repository OR we use Specifications.
    Page<T> findAll(String keyword, Pageable pageable);
}
