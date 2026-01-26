package frontEnd.setting.repository;

import com.onlineStoreCom.entity.setting.state.Country.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {

    List<Country> findAllByOrderByNameAsc();

    @Query("SELECT c FROM Country c WHERE c.code = ?1")
    Country findByCode(String code);


    @Query("SELECT c FROM Country c ORDER BY c.name ASC")
    List<Country> listAll();


}
