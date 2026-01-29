package com.onlineStore.admin.order;

import com.onlineStore.admin.security.StoreBackendUserDetails;
import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStore.admin.setting.settingBag.CurrencySettingBag;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStoreCom.entity.exception.OrderNotFoundException;
import com.onlineStoreCom.entity.order.Order;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OrderController.class);

    private final String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;
    // AG-CLEANUP: ProductService is now used by OrderService, not Controller
    // (unless needed for other views)
    // Checking usages below: editOrder uses listAllCountries via OrderService.
    // listByPage uses orderService.
    // saveOrder uses ProductService INDIRECTLY via updateProductDetails.
    // So we can remove ProductService autowire?
    // Wait, updateProductDetails (now in Service) needs it. OrderController doesn't
    // seem to need it explicitly elsewhere.
    // Let's comment it out to see if it breaks anything (Verification will catch
    // it).
    // @Autowired
    // private ProductService productService;

    @GetMapping("/orders")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    /**
     * Tenant: My Orders Alias
     */
    @GetMapping("/my-orders")
    public String viewMyOrders() {
        return defaultRedirectURL;
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum,
            @RequestParam(name = "sortField", defaultValue = "orderTime") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "keyWord", required = false) String keyWord,
            HttpServletRequest request, @AuthenticationPrincipal StoreBackendUserDetails loggedUser) {

        orderService.listByPage(pageNum, helper);
        loadCurrencySetting(request);

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
            return "orders/orders_shipper";
        }

        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        CurrencySettingBag currencySettings = settingService.getCurrencySettings();

        for (Setting setting : currencySettings.list()) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
                                   HttpServletRequest request, @AuthenticationPrincipal StoreBackendUserDetails loggedUser) {
        try {
            Order order = orderService.get(id);
            loadCurrencySetting(request);
            boolean isVisibleForAdminOrSalesperson = loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson");

            model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }

    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return defaultRedirectURL;
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
                            HttpServletRequest request) {
        try {
            Order order = orderService.get(id);

            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";

        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }

    }

    @PostMapping("/order/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
        String countryName = request.getParameter("countryName");
        order.setCountry(countryName);

        Long tenantId = TenantContext.getTenantId();
        order.setTenantId(tenantId);

        // AG-REFACTOR-ORDER-003: Delegated to Service
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        orderService.updateProductDetails(order, detailIds, productIds, productPrices, productDetailCosts, quantities,
                productSubtotals, productShipCosts);
        orderService.updateOrderTracks(order, trackIds, trackStatuses, trackDates, trackNotes);

        orderService.save(order);

        ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");

        return defaultRedirectURL;
    }

    // AG-CLEANUP: Private methods moved to Service

}
