package com.onlineStoreCom.repo.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseTenantRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Optional<T> findByIdAndTenantId(ID id, Long tenantId);

    List<T> findAllByTenantId(Long tenantId);

    @Override
    <S extends T> S save(S entity);

    @Override
    Optional<T> findById(ID id);

    @Override
    List<T> findAll();
}
