document.getElementById('planForm').addEventListener('submit', function(e) {
    e.preventDefault();
    if (!this.checkValidity()) {
        e.stopPropagation();
        this.classList.add('was-validated');
        return;
    }

    const formData = new FormData(this);
    const data = Object.fromEntries(formData.entries());
    const method = this.dataset.method || 'POST';
    const url = this.action;

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при сохранении плана подписки');
        return response.json();
    })
    .then(plan => {
        showNotification('План подписки успешно сохранен', 'success');
        window.location.href = `/admin/products/${plan.productId}`;
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
});

function initCharts(statistics) {
    if (!statistics) return;

    // График подписчиков
    new Chart(document.getElementById('subscribersChart'), {
        type: 'line',
        data: {
            labels: Object.keys(statistics.subscriberGrowth),
            datasets: [{
                label: 'Подписчики',
                data: Object.values(statistics.subscriberGrowth),
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1,
                fill: true
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

    new Chart(document.getElementById('revenueChart'), {
        type: 'bar',
        data: {
            labels: Object.keys(statistics.revenueGrowth),
            datasets: [{
                label: 'Доход (BYN)',
                data: Object.values(statistics.revenueGrowth),
                backgroundColor: 'rgba(255, 159, 64, 0.2)',
                borderColor: 'rgb(255, 159, 64)',
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

document.addEventListener('DOMContentLoaded', function() {
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