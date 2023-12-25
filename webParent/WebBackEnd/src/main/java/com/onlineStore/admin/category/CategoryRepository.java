package com.onlineStore.admin.category;


import com.onlineStoreCom.entity.prodact.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {



    @Query("UPDATE Category u set  u.enable=?2 WHERE u.id = ?1 ")
    @Modifying
    void enableCategory(Long id, boolean enable);

    @Query("SELECT u FROM Category u WHERE  CONCAT(u.id, ' ', u.name, ' ', u.alias, ' ',"+
            "u.parent) LIKE %?1%")
    Page<Category> findAll(String keyword, Pageable pageable);


}
