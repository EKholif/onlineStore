// Billing Dashboard Logic using Chart.js

$(document).ready(function () {
    loadDashboardData();
});

function loadDashboardData() {
    $.get("/api/billing/reports", function (data) {
        console.log("Billing Data Loaded:", data);

        // Update Summary Cards
        $("#totalRevenueDisplay").text("$" + data.totalRevenue.toFixed(2));
        $("#totalTxDisplay").text(data.transactionCount);

        // Render Charts
        renderRevenueChart(data.revenueByTenant);
        renderTypeChart(data.transactionsByType);
    }).fail(function () {
        console.error("Failed to load billing reports.");
        $("#totalRevenueDisplay").text("Error");
    });
}

function renderRevenueChart(revenueByTenant) {
    if (!revenueByTenant) return; // Tenant view might not have this

    const ctx = document.getElementById('revenueChart').getContext('2d');

    const labels = Object.keys(revenueByTenant).map(id => "Tenant " + id);
    const values = Object.values(revenueByTenant);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Commission Revenue ($)',
                data: values,
                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function renderTypeChart(transactionsByType) {
    if (!transactionsByType) return;

    const ctx = document.getElementById('typeChart').getContext('2d');

    const labels = Object.keys(transactionsByType);
    const values = Object.values(transactionsByType);

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                label: 'Transaction Types',
                data: values,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(255, 206, 86, 0.6)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true
        }
    });
}
