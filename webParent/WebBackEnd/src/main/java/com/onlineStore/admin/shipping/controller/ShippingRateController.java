package com.onlineStore.admin.shipping.controller;

import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.helper.SortingInfo;
import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStore.admin.shipping.service.ShippingRateService;
import com.onlineStore.admin.setting.state.StateRepository;
import com.onlineStore.admin.security.tenant.TenantContext;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.setting.state.State;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ShippingRateController {

    public static final int RATES_PER_PAGE = 4;
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/shipping-rate")
    public ModelAndView viewHome() {
        return listByPage(1, "country", "desc", null);
    }

    @GetMapping("/shipping-rate/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable("pageNum") int pageNum, @RequestParam(name = "sortField", defaultValue = "country") String sortField,
                                   @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir, @RequestParam(name = "keyWord", required = false) String keyWord) {

        ModelAndView model = new ModelAndView("shipping-rate/shipping-rate");

        SortingInfo sorting = new SortingInfo(sortField, sortDir);

        Page<ShippingRate> page = shippingRateService.findAll(keyWord, pageNum, RATES_PER_PAGE, sorting.getSort());
        List<ShippingRate> list = page.getContent();

        ShippingRatePagingHelper helper = new ShippingRatePagingHelper(model, "shippingRates", "Shipping Rates", "shipping-rate", list, page, keyWord, pageNum, RATES_PER_PAGE, sorting);

        return helper.listByPage();
    }


    @GetMapping("/new-shipping-rate-form")
    public ModelAndView newShippingForm() {
        ModelAndView model = new ModelAndView("shipping-rate/new-shipping-rate-form");

        ShippingRate shippingRate = new ShippingRate();
        List<Country> countries = countryRepository.findAllByOrderByNameAsc();
        List<State> states = stateRepository.findAllByOrderByNameAsc();


        model.addObject("allStates", states);

        NewShippingRateHelper helper = new NewShippingRateHelper(model, "listItems", "shipping-rate", "Shipping Rate", countries);


        return helper.newForm("shippingRate", shippingRate);

    }

    @PostMapping("/shipping-rate/save-new-shipping-rate")
    public ModelAndView saveNewUCategory(@ModelAttribute ShippingRate shippingRate, RedirectAttributes redirectAttributes) throws IOException {
        redirectAttributes.addFlashAttribute("message", " New Shipping Rate has been saved successfully.  ");
        Long tenantId = TenantContext.getTenantId();
        shippingRate.setTenantId(tenantId);

        shippingRateService.saveShippingRate(shippingRate);
        return new ModelAndView("redirect:/shipping-rate");
    }


    @GetMapping("/shipping-rate/edit/{id}")
    public ModelAndView editShippingRate(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            ShippingRate shippingRate = shippingRateService.findById(id);
            ModelAndView model = new ModelAndView("shipping-rate/new-shipping-rate-form");

            List<Country> countries = countryRepository.findAllByOrderByNameAsc();
            List<State> states = stateRepository.findAllByOrderByNameAsc();
            model.addObject("allStates", states);


            EditForm helper = new EditForm(model, "listItems", "shipping-rate", "Shipping Rate", countries);

            return helper.editForm("shippingRate", shippingRate, id);


        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "Shipping Rate not found");
            return new ModelAndView("redirect:/shipping-rate");
        }
    }

    @PostMapping("/save-edit-shipping-rate/")
    public ModelAndView saveUpdatedRate(@RequestParam("id") Integer id, @ModelAttribute ShippingRate shippingRate, RedirectAttributes redirectAttributes) throws IOException, CategoryNotFoundException {

        redirectAttributes.addFlashAttribute("message", "Shipping Rate ID " + id + " updated successfully.");


        ShippingRate existingRate = shippingRateService.findById(id);

        BeanUtils.copyProperties(shippingRate, existingRate, "id");
        shippingRateService.saveShippingRate(existingRate);


        return new ModelAndView("redirect:/shipping-rate");
    }

    @GetMapping("/shipping-rate/{id}/enable/{status}")
    public ModelAndView updateRateStatus(@PathVariable("id") int id, @PathVariable("status") boolean enable, RedirectAttributes redirectAttributes) {

        shippingRateService.UpdateRateEnableStatus(id, enable);
        String statusText = enable ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "Rate ID " + id + " has been " + statusText + ".");

        return new ModelAndView("redirect:/shipping-rate");
    }

    @GetMapping("/delete-shipping-rate/{id}")
    public ModelAndView deleteShippingRate(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        try {
            if (shippingRateService.existsById(id)) {
                shippingRateService.delete(id);
                redirectAttributes.addFlashAttribute("message", "Rate ID " + id + " deleted.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Rate ID " + id + " not found.");
            }

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "Error deleting rate: " + ex.getMessage());
        }

        return new ModelAndView("redirect:/shipping-rate");
    }
}
