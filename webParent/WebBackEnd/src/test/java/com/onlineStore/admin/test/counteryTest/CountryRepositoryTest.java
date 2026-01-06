package com.onlineStore.admin.test.counteryTest;

import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
public class CountryRepositoryTest {

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        com.onlineStoreCom.tenant.TenantContext.setTenantId(1L);
    }

    @Autowired
    private CountryRepository repo;

    @Test
    public void testCreateCountry() {
        Country country = repo.save(new Country("China", "CN"));
        assertThat(country).isNotNull();
        assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreate2Country() {
        Country country = repo.save(new Country("Egypt", "EGY"));
        assertThat(country).isNotNull();
        assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCountries() {
        repo.save(new Country("France", "FR"));
        List<Country> listCountries = repo.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);

        assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    public void testListAllCountries() {
        repo.save(new Country("Germany", "DE"));
        Iterable<Country> listCountries = repo.findAllByOrderByNameAsc();
        System.out.println("🔥 test started 🔥");
        for (Country country : listCountries) {
            System.out.println(" - name   -" + country.getName());
            System.out.println(" - id   -" + country.getId());
            System.out.println(" - code   -" + country.getCode());
            System.out.println("🔥 test started 🔥");
        }
    }

    @Test
    public void testUpdateCountry() {
        Country country = repo.save(new Country("India", "IN"));
        String name = "Republic of India";

        country.setName(name);
        Country updatedCountry = repo.save(country);

        assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCountry() {
        Country country = repo.save(new Country("Japan", "JP"));
        Optional<Country> found = repo.findById(country.getId());
        assertThat(found).isPresent();
    }

    @Test
    public void testDeleteCountry() {
        Country country = repo.save(new Country("Kyoto", "KY"));
        Integer id = country.getId();
        repo.deleteById(id);

        Optional<Country> findById = repo.findById(id);
        assertThat(findById).isEmpty();
    }
}