// Инициализация графиков на дашборде
document.addEventListener('DOMContentLoaded', function() {
    // Получаем данные статистики
    const statisticsElement = document.getElementById('statisticsData');
    if (statisticsElement) {
        const statistics = JSON.parse(statisticsElement.textContent);
        initDashboardCharts(statistics);
    }

    // Инициализация тултипов
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Анимация для карточек
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
        card.classList.add('animated-card');
    });
});

function initDashboardCharts(statistics) {
    // График роста пользователей
    if (document.getElementById('userGrowthChart')) {
        new Chart(document.getElementById('userGrowthChart').getContext('2d'), {
            type: 'line',
            data: {
                labels: Object.keys(statistics.userGrowth),
                datasets: [{
                    label: 'Новые пользователи',
                    data: Object.values(statistics.userGrowth),
                    borderColor: '#4e73df',
                    backgroundColor: 'rgba(78, 115, 223, 0.05)',
                    tension: 0.3,
                    fill: true
                }]
            },
            options: {
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    // График доходов
    if (document.getElementById('revenueChart')) {
        new Chart(document.getElementById('revenueChart').getContext('2d'), {
            type: 'line',
            data: {
                labels: Object.keys(statistics.revenueGrowth),
                datasets: [{
                    label: 'Доход (BYN)',
                    data: Object.values(statistics.revenueGrowth),
                    borderColor: '#1cc88a',
                    backgroundColor: 'rgba(28, 200, 138, 0.05)',
                    tension: 0.3,
                    fill: true
                }]
            },
            options: {
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        },
                        ticks: {
                            callback: function(value) {
                                return value + ' BYN';
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }
}

// Общие функции для админ-панели
function showNotification(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    const container = document.getElementById('toast-container') || createToastContainer();
    container.appendChild(toast);

    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();

    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'position-fixed bottom-0 end-0 p-3';
    container.style.zIndex = '1050';
    document.body.appendChild(container);
    return container;
}

// Форматирование данных
function formatDate(date) {
    return new Date(date).toLocaleDateString('ru-RU', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

function formatMoney(amount) {
    return new Intl.NumberFormat('ru-BY', {
        style: 'currency',
        currency: 'BYN'
    }).format(amount);
}

// Обработка действий в таблицах
function handleTableAction(event, url, method, confirmMessage) {
    event.preventDefault();
    
    if (confirmMessage && !confirm(confirmMessage)) {
        return;
    }
    
    fetch(url, {
        method: method,
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при выполнении действия');
        window.location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification(error.message, 'danger');
    });
} 