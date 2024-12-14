document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    form.addEventListener('submit', function(event) {
        if (password.value || confirmPassword.value) {
            if (password.value !== confirmPassword.value) {
                event.preventDefault();
                alert('Пароли не совпадают');
            } else if (password.value.length < 6) {
                event.preventDefault();
                alert('Пароль должен содержать минимум 6 символов');
            }
        }
    });
}); 