$(document).ready(function () {
    loadDashboardStats();
    loadActivityChart();
});

function loadDashboardStats() {
    $.ajax({
        type: "GET",
        url: "/api/dashboard/stats",
        success: function (response) {
            console.log("Dashboard Stats:", response);
            if (response.type === "TENANT") {
                $("#totalOrders").text(response.orderCount);
                $("#totalRevenue").text(formatCurrency(response.revenue));
            } else {
                // Should not happen on store dashboard, but just in case
                console.warn("Received Root stats on Tenant Dashboard");
            }
        },
        error: function (xhr, status, error) {
            console.error("Error loading stats:", error);
            $("#totalOrders").text("-");
            $("#totalRevenue").text("-");
        }
    });
}

function formatCurrency(amount) {
    if (amount === undefined || amount === null) return "$0.00";
    return new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'}).format(amount);
}

function loadActivityChart() {
    const ctx = document.getElementById('storeChart').getContext('2d');
    // Placeholder chart - In real app, fetch historical data from /api/dashboard/activity
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            datasets: [{
                label: 'Visitors',
                data: [12, 19, 3, 5, 2, 3, 10], // Dummy data
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1
            },
                {
                    label: 'Orders',
                    data: [2, 5, 1, 2, 0, 1, 4], // Dummy data
                    borderColor: 'rgb(255, 99, 132)',
                    tension: 0.1
                }]
        },
        options: {
            responsive: true,
            scales: {
                y: {beginAtZero: true}
            }
        }
    });
}
