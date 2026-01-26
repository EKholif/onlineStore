package frontEnd.shipping;


import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {



	// AG-TEN-FIX: Global query (cross-tenant risk) - case-insensitive
	@Query("SELECT c FROM ShippingRate c WHERE c.country = ?1 AND UPPER(c.stateName) = UPPER(?2)")
	 ShippingRate findByCountryAndState(Country country, String state);

	// AG-TEN-FIX: Tenant-scoped query for shipping rates - case-insensitive & trim-safe
	@Query("SELECT c FROM ShippingRate c WHERE c.country = ?1 AND UPPER(TRIM(c.stateName)) = UPPER(TRIM(?2)) AND c.tenantId = ?3")
	ShippingRate findByCountryAndStateAndTenantId(Country country, String state, Long tenantId);
}
