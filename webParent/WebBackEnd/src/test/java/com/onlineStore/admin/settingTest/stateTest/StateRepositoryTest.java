package com.onlineStore.admin.settingTest.stateTest;

import com.onlineStore.admin.setting.state.StateRepository;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import com.onlineStoreCom.tenant.TenantContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class StateRepositoryTest {

    @Autowired
    private StateRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        TenantContext.setTenantId(1L);
    }

    private Country createAndPersistCountry(String name, String code) {
        Country country = new Country(name, code);
        return entityManager.persist(country);
    }

    private State createAndPersistState(String name, Country country) {
        State state = new State(name, country);
        return repo.save(state);
    }

    @Test
    public void testCreateStatesInIndia() {
        Country country = createAndPersistCountry("India", "IN");

        State state = repo.save(new State("Karnataka", country));
        repo.save(new State("Punjab", country));
        repo.save(new State("Uttar Pradesh", country));
        repo.save(new State("West Bengal", country));

        assertThat(state).isNotNull();
        assertThat(state.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateStatesInUS() {
        Country country = createAndPersistCountry("USA", "US");

        State state = repo.save(new State("California", country));
        repo.save(new State("Texas", country));
        repo.save(new State("New York", country));
        repo.save(new State("Washington", country));

        assertThat(state).isNotNull();
        assertThat(state.getId()).isGreaterThan(0);
    }

    @Test
    public void testListStatesByCountry() {
        Country country = createAndPersistCountry("France", "FR");
        repo.save(new State("Paris", country));
        repo.save(new State("Lyon", country));

        List<State> listStates = repo.findByCountryOrderByNameAsc(country);

        listStates.forEach(System.out::println);

        Assertions.assertThat(listStates.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateState() {
        Country country = createAndPersistCountry("Germany", "DE");
        State state = createAndPersistState("Bavaria", country);

        String stateName = "Bavaria Updated";
        state.setName(stateName);

        State updatedState = repo.save(state);

        assertThat(updatedState.getName()).isEqualTo(stateName);
    }

    @Test
    public void testGetState() {
        Country country = createAndPersistCountry("Japan", "JP");
        State state = createAndPersistState("Tokyo", country);

        Optional<State> findById = repo.findById(state.getId());
        Assertions.assertThat(findById.isPresent());
    }

    @Test
    public void testDeleteState() {
        Country country = createAndPersistCountry("Italy", "IT");
        State state = createAndPersistState("Rome", country);
        Integer stateId = state.getId();

        repo.deleteById(stateId);

        Optional<State> findById = repo.findById(stateId);
        Assertions.assertThat(findById.isEmpty());
    }
}