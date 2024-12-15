document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded');
    
    // Проверяем, есть ли productId в скрытом поле
    const productIdInput = document.querySelector('input#productId[type="hidden"]');
    if (productIdInput) {
        console.log('Product ID found:', productIdInput.value);
        // Если есть - сразу показываем рекомендацию
        showRecommendation(productIdInput.value);
    } else {
        // Если нет - вешаем обработчик на select
        const productSelect = document.querySelector('select#productId');
        console.log('Product select:', productSelect);
        
        if (productSelect) {
            productSelect.addEventListener('change', function(e) {
                console.log('Select changed:', e.target.value);
                const productId = this.value;
                if (productId) {
                    showRecommendation(productId);
                }
            });
        }
    }
});

function showNotification(message, type = 'info') {
    const toast = document.getElementById('notificationToast');
    const toastTitle = document.getElementById('toastTitle');
    const toastMessage = document.getElementById('toastMessage');
    
    // Настраиваем внешний вид в зависимости от типа
    toast.className = 'toast';
    switch(type) {
        case 'error':
            toast.classList.add('bg-danger', 'text-white');
            toastTitle.textContent = 'Ошибка';
            break;
        case 'warning':
            toast.classList.add('bg-warning');
            toastTitle.textContent = 'Внимание';
            break;
        case 'success':
            toast.classList.add('bg-success', 'text-white');
            toastTitle.textContent = 'Успешно';
            break;
        default:
            toast.classList.add('bg-info', 'text-white');
            toastTitle.textContent = 'Информация';
    }
    
    toastMessage.textContent = message;
    
    // Показываем уведомление
    if (!toast._bsToast) {
        toast._bsToast = new bootstrap.Toast(toast, {
            autohide: true,
            delay: 5000
        });
    }
    toast._bsToast.show();
}

function showRecommendation(productId) {
    console.log('Fetching recommendation for product:', productId);
    fetch(`/organiser/subscription-plans/recommendation?productId=${productId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        credentials: 'same-origin'
    })
    .then(response => {
        console.log('Response:', response);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Recommendation data:', data);
        if (data.error) {
            console.error('Recommendation error:', data.error);
            showNotification(data.error, 'error');
            return;
        }

        // Если нет рекомендации, показываем только объяснение
        if (!data.recommendedPrice && !data.recommendedPeriodDays) {
            showNotification(
                data.explanation || 'Недостаточно данных для формирования рекомендации', 
                'warning'
            );
            return;
        }
        
        // Заполняем данные в модальном окне
        document.getElementById('recommendationExplanation').textContent = data.explanation || '';
        document.getElementById('recommendedPrice').value = data.recommendedPrice || '';
        document.getElementById('recommendedPeriod').value = data.recommendedPeriodDays || '';
        
        // Безопасное отображение числовых значений
        const expectedRevenue = data.expectedMonthlyRevenue ? 
            parseFloat(data.expectedMonthlyRevenue).toFixed(2) : 'Н/Д';
        const conversionRate = data.conversionRate ? 
            (parseFloat(data.conversionRate) * 100).toFixed(1) : 'Н/Д';
        
        document.getElementById('expectedRevenue').textContent = expectedRevenue;
        document.getElementById('conversionRate').textContent = conversionRate;
        
        // Показываем модальное окно только если есть рекомендация
        const modalElement = document.getElementById('recommendationModal');
        if (!modalElement._bsModal) {
            modalElement._bsModal = new bootstrap.Modal(modalElement);
        }
        modalElement._bsModal.show();
    })
    .catch(error => {
        console.error('Error getting recommendation:', error);
        showNotification(
            'Не удалось получить рекомендации. Пожалуйста, попробуйте позже.',
            'error'
        );
    });
}

function applyRecommendation() {
    console.log('Applying recommendation');
    document.getElementById('price').value = document.getElementById('recommendedPrice').value;
    document.getElementById('periodDays').value = document.getElementById('recommendedPeriod').value;
    
    const modalElement = document.getElementById('recommendationModal');
    if (modalElement._bsModal) {
        modalElement._bsModal.hide();
    }
} 