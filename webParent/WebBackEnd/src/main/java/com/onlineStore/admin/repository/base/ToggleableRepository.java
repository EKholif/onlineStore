package com.onlineStore.admin.repository.base;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface ToggleableRepository<T, ID> extends Repository<T, ID> {

    @Query("UPDATE #{#entityName} e SET e.enabled = ?2 WHERE e.id = ?1")
    @Modifying
    @Transactional
    void updateEnabledStatus(ID id, boolean enabled);
}
