function calculateTotal() {
    let totalBefore = 0;
    let totalAfter = 0;

    document.querySelectorAll(".quantity-control").forEach(container => {
        const productId = container.dataset.productId || "";

        const quantityInput = document.getElementById(`quantity${productId}`);
        const originalEl = document.getElementById(`original${productId}`);
        const discountedEl = document.getElementById(`discounted${productId}`);

        const quantity = parseInt(quantityInput?.value) || 0;
        const originalPrice = parseFloat(originalEl?.dataset.price?.replace(",", ".")) || 0;
        const finalPrice = parseFloat(discountedEl?.dataset.price?.replace(",", ".")) || originalPrice;

        totalBefore += originalPrice * quantity;
        totalAfter += finalPrice * quantity;

    });

    document.getElementById("totalBefore").textContent = formatCurrency(totalBefore);
    document.getElementById("totalAfter").textContent = formatCurrency(totalAfter);
}

function formatCurrency(amount) {
    const formatted = amount.toLocaleString(undefined, {
        minimumFractionDigits: decimalDigits,
        maximumFractionDigits: decimalDigits
    });

    return currencyPosition === "before" ? `${currencySymbol}${formatted}` : `${formatted}${currencySymbol}`;
}

function formatCurrency1(amount) {
    return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}


function clearCurrencyFormat(numberString) {
    result = numberString.replaceAll(thousandsSeparator, "");
    return result.replaceAll(decimalSeparator, ".");
}
