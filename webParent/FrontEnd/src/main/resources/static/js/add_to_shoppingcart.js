$(document).ready(function () {


	$(document).on("click", ".btnAddToCart", function () {
		const $row = $(this).closest(".row");          // صف المنتج الحالي
		const productId = $row.find(".productId").data("product-id");
		const quantity  = $row.find(".qtyInput").val(); // حقل الكمية داخل نفس الصف
		const url = contextPath + "cart/add/" + productId + "/" + quantity;



		$.ajax({
			type: "POST",
			url: url,
			beforeSend: function (xhr) {
				xhr.setRequestHeader(csrfHeaderName, csrfValue);
			}
		})
			.done(function (response) {
				showmodalDialog("Shopping Cart", response);
			})
			.fail(function () {
				showErrorModal("Error while adding product to shopping cart.");
			});
	});
});
