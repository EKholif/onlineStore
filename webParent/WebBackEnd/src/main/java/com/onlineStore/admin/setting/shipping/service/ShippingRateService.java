package com.onlineStore.admin.setting.shipping.service;




import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.controller.PagingAndSortingHelper;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStore.admin.helper.SortingInfo;
import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStore.admin.setting.shipping.repository.ShippingRateRepository;
import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.exception.ShippingRateNotFoundException;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional
public class ShippingRateService {
	private static final int DIM_DIVISOR = 139;
	@Autowired private ShippingRateRepository shipRepo;
	@Autowired private CountryRepository countryRepo;
	@Autowired private ProductRepository productRepo;

	public Page<ShippingRate> findAll(String keyword, int pageNum, int pageSize, Sort sort) {
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

		if (keyword != null && !keyword.isEmpty()) {
			return shipRepo.findAll(keyword, pageable);
		}

		return shipRepo.findAll(pageable);
	}





	public void UpdateRateEnableStatus(int id, Boolean enable){
		shipRepo.updateCODSupport(id, enable);

	}


	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);

		}
		shipRepo.deleteById(String.valueOf(id));
	}


	public Boolean existsById(Integer id) {
		return shipRepo.findById(String.valueOf(id)).isPresent();
	}

	public ShippingRate findById(Integer id) throws CategoryNotFoundException {
		try {


			return shipRepo.findById(String.valueOf(id)).get();


		} catch (NoSuchElementException ex) {


			throw new CategoryNotFoundException("Could not find any shipping rate with ID " + id);
		}
	}

	public ShippingRate saveShippingRate(ShippingRate shippingRate) {
		return shipRepo.saveAndFlush(shippingRate);

	}




//	public List<Country> listAllCountries() {
//		return countryRepo.findAllByOrderByNameAsc();
//	}
	
//	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
//		ShippingRate rateInDB = shipRepo.findByCountryAndState(
//				rateInForm.getCountry().getId(), rateInForm.getState());
//		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
//		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);
//
//		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
//			throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
//						+ rateInForm.getCountry().getName() + ", " + rateInForm.getState());
//		}
//		shipRepo.save(rateInForm);
//	}
//
//	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
//		try {
//			return shipRepo.findById(id).get();
//		} catch (NoSuchElementException ex) {
//			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
//		}
//	}
//
//	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
//		Long count = shipRepo.countById(id);
//		if (count == null || count == 0) {
//			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
//		}
//
//		shipRepo.updateCODSupport(id, codSupported);
//	}

//
//	public float calculateShippingCost(Integer productId, Integer countryId, String state)
//			throws ShippingRateNotFoundException {
//		ShippingRate shippingRate = shipRepo.findByCountryAndState(countryId, state);
//
//		if (shippingRate == null) {
//			throw new ShippingRateNotFoundException("No shipping rate found for the given "
//					+ "destination. You have to enter shipping cost manually.");
//		}
//
//		Product product = productRepo.findById(productId).get();
//
//		float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
//		float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
//
//		return finalWeight * shippingRate.getRate();
//	}
}
