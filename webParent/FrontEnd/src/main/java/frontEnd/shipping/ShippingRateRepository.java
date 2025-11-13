package frontEnd.shipping;


import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {



	@Query("SELECT c FROM ShippingRate c WHERE c.country = ?1 AND c.stateName =?2 " )
	 ShippingRate findByCountryAndState(Country country, String state);
}
