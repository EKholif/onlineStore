package com.onlineStore.admin.settingTest.shippingTest;

import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStore.admin.shipping.repository.ShippingRateRepository;
import com.onlineStore.admin.setting.state.StateRepository;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
class ShippingRateRepositoryTest {
    @Autowired
    private ShippingRateRepository repo;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private TestEntityManager entityManager;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        com.onlineStoreCom.tenant.TenantContext.setTenantId(1L);
    }

    @Test
    public void testCreateShippingRateInIndia() {
        Country country = new Country("Test Country", "TC");
        country.setTenantId(1L);
        country = entityManager.persist(country);

        State getState = new State("Test State", country);
        getState.setTenantId(1L);
        getState = entityManager.persist(getState);

        String state = getState.getName();
        float rate = 4.99f;

        System.out.println("البلد: " + country.getName());
        System.out.println("المحافظة: " + state);

        ShippingRate shippingRate = new ShippingRate(rate);
        shippingRate.setCountry(country);
        shippingRate.setStateName(state);
        shippingRate.setTenantId(1L);

        // إضافة باقي القيم لو كانت مطلوبة
        shippingRate.setDays(3); // مثلاً يومين أو ٣ حسب النظام عندك
        shippingRate.setCodSupported(true); // هل يدعم الدفع عند الاستلام

        repo.save(shippingRate);
    }

    @Test
    public void testListAll() {

        List<ShippingRate> shippingRateList = repo.findAll();

        shippingRateList.forEach(c -> System.out.println(c.getCountry()));

        assertThat(shippingRateList.size()).isGreaterThan(0);

    }

    @Test
    void testFindAllByKeyword() {
        String keyword = "China"; // يبحث عن "Cairo"

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShippingRate> result = repo.findAll(keyword, pageable);
        result.forEach(c -> System.out.println(c.getCountry() + "  Shipping Rate  :  " + c.getRate()));

        // assertThat(result.getContent()).isNotNull();
        // assertThat(result.getContent().get(0).getState().getName()).containsIgnoringCase("cai");
    }

    // @Test
    // void testUpdateStateId() {
    // List<String> states = repo.findAllNonNullStateNames();
    // for (String c : states) {
    // State state = stateRepository.findByName(c);
    // repo.updateStateName(c, state);
    // }
    // }

    @Test
    void testUptStateId() {
        List<Object[]> results = repo.findAllStateNameAndCountry();

        for (Object[] row : results) {
            String stateName = (String) row[0];
            Country country = (Country) row[1]; // حسب نوع الحقل في الكيان
            State state = stateRepository.findByName(stateName);

            if (stateName == null)
                continue;

            if (state != null) {
                System.out.println("State: " + stateName + ", Id: " + state.getId());
                ShippingRate shippingRate = repo.findByStateName(stateName);
                shippingRate.setStateName(stateName);
                repo.save(shippingRate);
            } else {

                System.out.println("State: " + stateName + ", Id: ");
                State newState = new State();

                newState.setName(stateName);
                newState.setCountry(country);
                newState.setTenantId(1L);
                stateRepository.save(newState);

                ShippingRate shippingRate = repo.findByStateName(stateName);
                shippingRate.setStateName(stateName);
                repo.save(shippingRate);

            }

        }

    }

}
