$(document).ready(function () {

	$("#buttonAdd2Cart").on("click", function(evt) {
		addToCart();
	});
});

function addToCart() {

	quantity = $("#quantity").val();
	productId =$("#productId").data("product-id");


	url = contextPath + "cart/add/" + productId + "/" + quantity;


	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}



	}).done(function(response) {

		showmodalDialog("Shopping Cart", response);
	}).fail(function() {
		showErrorModal("Error while adding product to shopping cart. ");
	});
}