package frontEnd.brand;

import com.onlineStoreCom.entity.brand.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    Integer countById(Integer id);

    Brand findByName(String name);

//    Brand findByAlies(String a);

//    @Query("SELECT b FROM Brand b WHERE  CONCAT(b.id, ' ', b.name, ' ', b.logo ) LIKE %?1%")
//    Page<Brand> findAll(String keyword, Pageable pageable);
//
//    @Query("SELECT new Brand(b.id,b.name) FROM Brand b ORDER BY  b.name ASC")
//    List<Brand> findAll();





}
