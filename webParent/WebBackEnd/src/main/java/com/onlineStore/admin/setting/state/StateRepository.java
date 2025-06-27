package com.onlineStore.admin.setting.state;

import com.onlineStoreCom.entity.setting.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, String> {



    List<State>  findAllByOrderByNameAsc();

    List<State>findByCountryOrderByNameAsc(Country country);

}
