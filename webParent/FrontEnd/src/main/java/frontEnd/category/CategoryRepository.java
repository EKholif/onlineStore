package frontEnd.category;

import com.onlineStoreCom.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value = "SELECT c.* FROM categories c JOIN categories p ON c.parent_id = p.id "
            + "WHERE c.enabled = 1 AND p.enabled = 1 ORDER BY c.name ASC", nativeQuery = true)
    List<Category> findAllEnabled();

    @Query(value = "SELECT c.* FROM categories c JOIN categories p ON c.parent_id = p.id "
            + "WHERE c.enabled = 1 AND p.enabled = 1 AND c.parent_id = ?1", nativeQuery = true)
    Set<Category> getChildren(Integer categoryId);

    @Query(value = "SELECT p.* FROM categories c JOIN categories p ON c.parent_id = p.id "
            + "WHERE c.enabled = 1 AND p.enabled = 1 AND c.id = ?1", nativeQuery = true)
    Category getParent(Integer categoryId);

    @Query(value = "SELECT * FROM categories WHERE enabled = 1 AND parent_id IS NULL", nativeQuery = true)
    List<Category> notParentCategory();

    @Query(value = "SELECT c.* FROM categories c JOIN categories p ON c.parent_id = p.id "
            + "WHERE c.enabled = 1 AND p.enabled = 1 AND c.alias = ?1", nativeQuery = true)
    Category findByAliasEnabled(String alias);

    @Query(value = "SELECT c.* FROM categories c JOIN categories p ON c.parent_id = p.id "
            + "WHERE c.enabled = 1 AND p.enabled = 1 AND c.name = ?1", nativeQuery = true)
    Category findByNameEnabled(String name);

    @Query(value = "SELECT * FROM categories WHERE parent_id IS NULL AND enabled = 1 AND alias = ?1", nativeQuery = true)
    Category findByAliasParent(String alias);

}
