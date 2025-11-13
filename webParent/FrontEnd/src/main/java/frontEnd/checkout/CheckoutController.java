package frontEnd.checkout;


import com.onlineStoreCom.entity.address.Address;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.order.Order;
import com.onlineStoreCom.entity.order.PaymentMethod;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import frontEnd.checkout.paypal.PayPalApiException;
import frontEnd.checkout.paypal.PayPalService;
import frontEnd.customerAddress.AddressService;
import frontEnd.order.OrderService;
import frontEnd.setting.EmailSettingBag;
import frontEnd.setting.service.SettingService;
import frontEnd.setting.settingBag.CurrencySettingBag;
import frontEnd.setting.settingBag.PaymentSettingBag;
import frontEnd.shipping.ShippingRateService;
import frontEnd.shoppingCart.ShoppingCartService;
import frontEnd.utilites.ControllerHelper;
import frontEnd.utilites.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private AddressService addressService;
	@Autowired private ShippingRateService shipService;
	@Autowired private ShoppingCartService cartService;
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;
	@Autowired private PayPalService paypalService;
	
	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
		if (defaultAddress != null) {
			model.addAttribute("shippingAddress", defaultAddress.toString());
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
		} else {
			model.addAttribute("shippingAddress", customer.toString());
			shippingRate = shipService.getShippingRateForCustomer(customer);
		}
		
		if (shippingRate == null) {
			return "redirect:/cart";
		}
		
		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);



		cartItems.forEach(i -> {
			if (i.getShippingCost()  != 0.0f) {
				System.out.println(i.getShippingCost());
			}
		});

		String currencyCode = settingService.getCurrencyCode();
		System.out.println();

		PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
		String paypalClientId = paymentSettings.getClientID();
		
		model.addAttribute("paypalClientId", paypalClientId);
		model.addAttribute("currencyCode", currencyCode.trim());
		model.addAttribute("customer", customer);
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("cartItems", cartItems);
		
		return "checkout/checkout";
	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) 
			throws UnsupportedEncodingException, MessagingException {
		String paymentType = request.getParameter("paymentMethod");
		PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);




		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
		if (defaultAddress != null) {
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
		} else {
			shippingRate = shipService.getShippingRateForCustomer(customer);
		}
				
		List<CartItem> cartItems = cartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
		
		Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
		cartService.deleteByCustomer(customer);
		sendOrderConfirmationEmail(request, createdOrder);
		
		return "checkout/order_completed";
	}

	private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		
		String toAddress = order.getCustomer().getEmail();
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
		 String shippingAddress =   order.getShippingAddress();
		subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		DateFormat dateFormatter =  new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime = dateFormatter.format(order.getOrderTime());
		
		CurrencySettingBag currencySettings = settingService.getCurrencySettings();
		String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);
		
		content = content.replace("[[name]]", order.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(order.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]",shippingAddress);
		content = content.replace("[[total]]", totalAmount);
		content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
		
		helper.setText(content, true);
		mailSender.send(message);		
	}
	
	@PostMapping("/process_paypal_order")
	public String processPayPalOrder(HttpServletRequest request, Model model) 
			throws UnsupportedEncodingException, MessagingException {
		String orderId = request.getParameter("orderId");

		String pageTitle = "Checkout Failure";
		String message = null;
		
		try {
			if (paypalService.validateOrder(orderId)) {

				return placeOrder(request);
			} else {
				pageTitle = "Checkout Failure";
				message = "ERROR: Transaction could not be completed because order information is invalid";
			}
		} catch (PayPalApiException e) {
			message = "ERROR: Transaction failed due to error: " + e.getMessage();
		}
		
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("title", pageTitle);
		model.addAttribute("message", message);
		
		return "message";
	}
}





