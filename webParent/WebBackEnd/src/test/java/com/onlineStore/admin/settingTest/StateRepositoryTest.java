package com.onlineStore.admin.settingTest;


import com.onlineStore.admin.setting.state.StateRepository;
import com.onlineStoreCom.entity.setting.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class StateRepositoryTest {
    @Autowired
    private StateRepository repo;
    @Autowired private TestEntityManager entityManager;


    @Test
    public void testCreateStatesInIndia() {
        Integer countryId = 1;
        Country country = entityManager.find(Country.class, countryId);

		State state = repo.save(new State("Karnataka", country));
		State state1 = repo.save(new State("Punjab", country));
		State state2 = repo.save(new State("Uttar Pradesh", country));
        State state3 = repo.save(new State("West Bengal", country));

        assertThat(state).isNotNull();
        assertThat(state.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateStatesInUS() {
        Integer countryId = 2;
        Country country = entityManager.find(Country.class, countryId);

		State state = repo.save(new State("California", country));
		State stat1 = repo.save(new State("Texas", country));
		State state2 = repo.save(new State("New York", country));
        State state3 = repo.save(new State("Washington", country));

        assertThat(state).isNotNull();
        assertThat(state.getId()).isGreaterThan(0);
    }

    @Test
    public void testListStatesByCountry() {
        Integer countryId = 2;
        Country country = entityManager.find(Country.class, countryId);
        List<State> listStates = repo.findByCountryOrderByNameAsc(country);

        listStates.forEach(System.out::println);

        assertThat(listStates.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateState() {
        String stateId = "3";
        String stateName = "Tamil Nadu";
        State state = repo.findById(stateId).get();

        state.setName(stateName);
        State updatedState = repo.save(state);

        assertThat(updatedState.getName()).isEqualTo(stateName);
    }

    @Test
    public void testGetState() {
        String stateId = "1";
        Optional<State> findById = repo.findById(stateId);
        assertThat(findById.isPresent());
    }

    @Test
    public void testDeleteState() {
        String stateId = "8";
        repo.deleteById(stateId);

        Optional<State> findById = repo.findById(stateId);
        assertThat(findById.isEmpty());
    }


}
