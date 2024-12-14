document.addEventListener('DOMContentLoaded', function() {
    const chartConfig = {
        line: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    titleColor: '#000',
                    bodyColor: '#666',
                    borderColor: '#e3e6f0',
                    borderWidth: 1,
                    padding: 10,
                    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            if (context.parsed.y !== null) {
                                label += new Intl.NumberFormat('ru-RU').format(context.parsed.y);
                            }
                            return label;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        drawBorder: false
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        },
        pie: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    };

    initSubscriptionsChart();
    initStatusesChart();
    initRevenueChart();

    function initSubscriptionsChart() {
        const ctx = document.getElementById('subscriptionsChart');
        if (!ctx) return;

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: subscriptionsData.labels,
                datasets: [{
                    label: 'Новые подписки',
                    data: subscriptionsData.newSubscriptions,
                    borderColor: '#4e73df',
                    backgroundColor: 'rgba(78, 115, 223, 0.05)',
                    tension: 0.3
                }, {
                    label: 'Продления',
                    data: subscriptionsData.renewals,
                    borderColor: '#1cc88a',
                    backgroundColor: 'rgba(28, 200, 138, 0.05)',
                    tension: 0.3
                }]
            },
            options: chartConfig.line
        });
    }

    function initStatusesChart() {
        const ctx = document.getElementById('statusesChart');
        if (!ctx) return;

        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: ['Активные', 'Завершенные', 'Отмененные'],
                datasets: [{
                    data: [
                        statistics.activeSubscriptionsCount,
                        statistics.completedSubscriptionsCount,
                        statistics.canceledSubscriptionsCount
                    ],
                    backgroundColor: ['#1cc88a', '#4e73df', '#e74a3b']
                }]
            },
            options: chartConfig.pie
        });
    }

    function initRevenueChart() {
        const ctx = document.getElementById('revenueChart');
        if (!ctx) return;

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: revenueData.labels,
                datasets: [{
                    label: 'Доход',
                    data: revenueData.values,
                    borderColor: '#f6c23e',
                    backgroundColor: 'rgba(246, 194, 62, 0.05)',
                    tension: 0.3
                }]
            },
            options: {
                ...chartConfig.line,
                plugins: {
                    ...chartConfig.line.plugins,
                    tooltip: {
                        ...chartConfig.line.plugins.tooltip,
                        callbacks: {
                            label: function(context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed.y !== null) {
                                    label += new Intl.NumberFormat('ru-RU', {
                                        style: 'currency',
                                        currency: 'RUB'
                                    }).format(context.parsed.y);
                                }
                                return label;
                            }
                        }
                    }
                }
            }
        });
    }

    const cards = document.querySelectorAll('.stat-card');
    cards.forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
        card.classList.add('animated-card');
    });

    const numberElements = document.querySelectorAll('[data-format="number"]');
    numberElements.forEach(element => {
        const value = parseInt(element.textContent);
        element.textContent = new Intl.NumberFormat('ru-RU').format(value);
    });

    const currencyElements = document.querySelectorAll('[data-format="currency"]');
    currencyElements.forEach(element => {
        const value = parseFloat(element.textContent);
        element.textContent = new Intl.NumberFormat('ru-RU', {
            style: 'currency',
            currency: 'RUB'
        }).format(value);
    });
}); 