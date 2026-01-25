$(document).ready(function () {
    loadAdminStats();
    // loadAdminCharts(); // If we implement charts later
});

function loadAdminStats() {
    $.ajax({
        type: "GET",
        url: "/api/dashboard/stats",
        success: function (response) {
            if (response.type === "ROOT") {
                $("#tenantCount").text(response.tenantCount);
                $("#platformRevenue").text(formatCurrency(response.revenue));
                // response.productsCount if added
            } else {
                console.warn("Received Tenant stats on Admin Dashboard");
            }
        },
        error: function (xhr, status, error) {
            console.error("Error loading admin stats:", error);
            $("#tenantCount").text("-");
            $("#platformRevenue").text("-");
        }
    });
}

function formatCurrency(amount) {
    if (amount === undefined || amount === null) return "$0.00";
    return new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'}).format(amount);
}
