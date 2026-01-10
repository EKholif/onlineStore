package com.onlineStore.admin.category;

import com.onlineStoreCom.entity.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("UPDATE Category u set  u.enabled=?2 WHERE u.id = ?1 ")
    @Modifying
    void enableCategory(Integer id, boolean enable);

    @Query("UPDATE Category SET enabled=true")
    @Modifying
    @jakarta.transaction.Transactional
    void enableCategoryAll();

    @Query("SELECT u FROM Category u WHERE  CONCAT(u.id, ' ', u.name, ' ', u.alias) LIKE %?1%")
    Page<Category> findAll(String keyword, Pageable pageable);

    @Query("SELECT C FROM Category C WHERE C.parent.id IS NULL ")
    Page<Category> findRootCategories(Pageable pageable);

    Category findByName(String name);

    Category findByAlias(String alias);

    List<Category> findByParent(Category parent);

    // @Modifying
    // @Query(value = """
    // WITH RECURSIVE subcategories AS (
    // SELECT id FROM categories WHERE id = ?1
    // UNION ALL
    // SELECT c.id FROM categories c
    // INNER JOIN subcategories s ON c.parent_id = s.id
    // )
    // UPDATE categories SET enabled = false WHERE id IN (SELECT id FROM
    // subcategories)
    // """, nativeQuery = true)
    // void disableCategoryTree(Integer categoryId);

    // FIX: Remove parent if it belongs to a different tenant (Data Cleanup)
    @Modifying
    @Query(value = "UPDATE categories c JOIN categories p ON c.parent_id = p.id SET c.parent_id = NULL WHERE c.tenant_id != p.tenant_id", nativeQuery = true)
    void fixBrokenHierarchies();

}
