function blockProduct(id) {
    if (!confirm('Вы действительно хотите заблокировать этот продукт?')) {
        return;
    }

    fetch(`/api/products/${id}/toggle-status`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при блокировке продукта');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

function unblockProduct(id) {
    if (!confirm('Вы действительно хотите активировать этот продукт?')) {
        return;
    }

    fetch(`/api/products/${id}/toggle-status`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при активации продукта');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

function blockPlan(id) {
    if (!confirm('Вы действительно хотите заблокировать этот план подписки?')) {
        return;
    }

    fetch(`/api/subscription-plans/${id}/toggle-status`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при блокировке плана подписки');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

function unblockPlan(id) {
    if (!confirm('Вы действительно хотите активировать этот план подписки?')) {
        return;
    }

    fetch(`/api/subscription-plans/${id}/toggle-status`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при активации плана подписки');
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}); 