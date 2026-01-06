package frontEnd.category;

import com.onlineStoreCom.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.parent.enabled = true  ORDER BY c.name ASC")
    List<Category> findAllEnabled();

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.parent.enabled = true AND c.parent = ?1 ")
    Set<Category> getChildren(Category category);

    @Query("SELECT c.parent FROM Category c WHERE c.enabled = true AND c.parent.enabled = true AND c = ?1 ")
    Category getParent(Category category);

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.parent = null  ")
    List<Category> notParentCategory();

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.parent.enabled = true  AND c.alias = ?1")
    Category findByAliasEnabled(String alias);

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.parent.enabled = true  AND c.name = ?1")
    Category findByNameEnabled(String alias);

    @Query("SELECT c FROM Category c WHERE c.parent = null AND c.enabled = true AND c.alias = ?1")
    Category findByAliasParent(String alias);

}
