function togglePlanStatus(id) {
    fetch(`/organiser/subscription-plans/${id}/toggle`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => Promise.reject(err));
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            const statusBadge = document.querySelector(`#plan-${id} .status-badge`);
            const toggleButton = document.querySelector(`#plan-${id} .toggle-status-btn`);
            
            if (statusBadge) {
                statusBadge.textContent = data.active ? 'Активен' : 'Неактивен';
                statusBadge.className = `badge ${data.active ? 'bg-success' : 'bg-danger'} status-badge`;
            }
            
            if (toggleButton) {
                toggleButton.textContent = data.active ? 'Деактивировать' : 'Активировать';
                toggleButton.className = `btn btn-sm ${data.active ? 'btn-danger' : 'btn-success'} toggle-status-btn`;
            }
            
            showNotification(data.message, 'success');
        } else {
            showNotification(data.message || 'Произошла ошибка', 'danger');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification(error.message || 'Произошла ошибка при изменении статуса', 'danger');
    });
} 