function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'position-fixed bottom-0 end-0 p-3';
    container.style.zIndex = '1050';
    document.body.appendChild(container);
    return container;
}

function toggleOrganisationStatus(id) {
    fetch(`/organiser/organisations/${id}/toggle`, {
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
            const row = document.querySelector(`#organisation-${id}`);
            const statusBadge = row.querySelector('.badge');
            const toggleButton = row.querySelector('.toggle-status-btn');
            
            if (statusBadge) {
                statusBadge.textContent = data.active ? 'Активна' : 'Неактивна';
                statusBadge.className = `badge ${data.active ? 'bg-success' : 'bg-danger'}`;
            }
            
            if (toggleButton) {
                toggleButton.textContent = data.active ? 'Деактивировать' : 'Активировать';
                toggleButton.className = `btn btn-sm ${data.active ? 'btn-danger' : 'btn-success'} toggle-status-btn`;
            }
            
            const container = document.getElementById('toast-container') || createToastContainer();
            const toast = document.createElement('div');
            toast.className = `toast align-items-center text-white bg-success border-0`;
            toast.setAttribute('role', 'alert');
            toast.setAttribute('aria-live', 'assertive');
            toast.setAttribute('aria-atomic', 'true');
            
            toast.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">
                        ${data.message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            `;
            
            container.appendChild(toast);
            const bsToast = new bootstrap.Toast(toast);
            bsToast.show();
            
            toast.addEventListener('hidden.bs.toast', () => {
                toast.remove();
            });
        } else {
            showNotification(data.message || 'Произошла ошибка', 'danger');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification(error.message || 'Произошла ошибка при изменении статуса', 'danger');
    });
} 