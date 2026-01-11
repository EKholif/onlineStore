package com.onlineStore.admin.setting.state;

import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import com.onlineStoreCom.entity.setting.state.StateDTO;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository repo;

	@GetMapping("/states/list_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
		List<State> listStates = repo.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> result = new ArrayList<>();

        for (State state : listStates) {
			result.add(new StateDTO(state.getId(), state.getName()));
		}

        return result;
	}

    @PostMapping("/states/save")
	public String save(@RequestBody State state) {
		Long tenantId = TenantContext.getTenantId();
        // state.setTenantId(tenantId); // Removed: State is shared
		State savedState = repo.save(state);
		return String.valueOf(savedState.getId());
	}

    @DeleteMapping("/states/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
		repo.deleteById(id);
	}
}
