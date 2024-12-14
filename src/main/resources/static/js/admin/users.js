let deleteUserId = null;

function createUser() {
    const form = document.getElementById('createUserForm');
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    fetch('/api/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при создании пользователя');
        return response.json();
    })
    .then(() => {
        showNotification('Пользователь успешно создан', 'success');
        bootstrap.Modal.getInstance(document.getElementById('createUserModal')).hide();
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

function editUser(id) {
    fetch(`/api/users/${id}`)
        .then(response => response.json())
        .then(user => {
            document.getElementById('editUserId').value = user.id;
            document.getElementById('editEmail').value = user.email;
            document.getElementById('editFirstName').value = user.firstName;
            document.getElementById('editLastName').value = user.lastName;
            document.getElementById('editRole').value = user.role;
            
            const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
            modal.show();
        })
        .catch(error => showNotification('Ошибка при загрузке данных пользователя', 'danger'));
}

function updateUser() {
    const form = document.getElementById('editUserForm');
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    const id = data.id;
    delete data.id;

    if (!data.password) delete data.password;

    fetch(`/api/users/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при обновлении пользователя');
        return response.json();
    })
    .then(() => {
        showNotification('Пользователь успешно обновлен', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
        window.location.reload();
    })
    .catch(error => {
        showNotification(error.message, 'danger');
    });
}

function blockUser(id) {
    fetch(`/api/users/${id}/block`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при блокировке пользователя');
        window.location.reload();
    })
    .catch(error => showNotification(error.message, 'danger'));
}

function unblockUser(id) {
    fetch(`/api/users/${id}/unblock`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при разблокировке пользователя');
        window.location.reload();
    })
    .catch(error => showNotification(error.message, 'danger'));
}

function deleteUser(id, email) {
    deleteUserId = id;
    document.getElementById('deleteUserEmail').textContent = email;
    const modal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
    modal.show();
}

// Подтверждение удаления пользователя
function confirmDeleteUser() {
    if (!deleteUserId) return;

    fetch(`/api/users/${deleteUserId}`, {
        method: 'DELETE',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Ошибка при удалении пользователя');
        window.location.reload();
    })
    .catch(error => showNotification(error.message, 'danger'));
}

// Очистка форм при закрытии модальных окон
document.getElementById('createUserModal').addEventListener('hidden.bs.modal', function () {
    document.getElementById('createUserForm').reset();
    document.getElementById('createUserForm').classList.remove('was-validated');
});

document.getElementById('editUserModal').addEventListener('hidden.bs.modal', function () {
    document.getElementById('editUserForm').reset();
    document.getElementById('editUserForm').classList.remove('was-validated');
}); 