function blockOrganisation(id) {
    if (!confirm('Вы действительно хотите заблокировать эту организацию?')) {
        return;
    }

    fetch(`/api/organisations/${id}/toggle-status`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при блокировке организации');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

function unblockOrganisation(id) {
    if (!confirm('Вы действительно хотите активировать эту организацию?')) {
        return;
    }

    fetch(`/api/organisations/${id}/toggle-status`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при активации организации');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('organisationForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            if (!form.checkValidity()) {
                e.stopPropagation();
                form.classList.add('was-validated');
                return;
            }

            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
            const method = form.dataset.method || 'POST';
            const url = form.action;

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            })
            .then(response => {
                if (!response.ok) throw new Error('Ошибка при сохранени�� организации');
                return response.json();
            })
            .then(org => {
                showNotification('Организация успешно сохранена', 'success');
                window.location.href = `/admin/organisations/${org.id}`;
            })
            .catch(error => {
                showNotification(error.message, 'danger');
            });
        });
    }
});

function initCharts(statistics) {
    if (!statistics) return;

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