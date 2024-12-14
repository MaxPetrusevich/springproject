function cancelSubscription(id) {
    if (!confirm('Вы уверены, что хотите отменить подписку?')) {
        return;
    }

    fetch(`/admin/subscriptions/${id}/cancel`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при отмене подписки');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

function renewSubscription(id) {
    if (!confirm('Вы уверены, что хотите продлить подписку?')) {
        return;
    }

    fetch(`/admin/subscriptions/${id}/renew`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при продлении подписки');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    // Инициализация подсказок
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    const statisticsData = document.getElementById('statisticsData');
    if (statisticsData) {
        const statistics = JSON.parse(statisticsData.textContent);
        initCharts(statistics);
    }
});

function initCharts(statistics) {
    new Chart(document.getElementById('paymentsChart'), {
        type: 'line',
        data: {
            labels: Object.keys(statistics.paymentHistory),
            datasets: [{
                label: 'Платежи',
                data: Object.values(statistics.paymentHistory),
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                }
            }
        }
    });

    new Chart(document.getElementById('usageChart'), {
        type: 'bar',
        data: {
            labels: Object.keys(statistics.usageHistory),
            datasets: [{
                label: 'Использование',
                data: Object.values(statistics.usageHistory),
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgb(54, 162, 235)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                }
            }
        }
    });
} 