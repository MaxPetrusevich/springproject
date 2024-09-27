function validateForm() {
    var firstName = document.getElementsByName('firstname')[0].value;
    var lastName = document.getElementsByName('surname')[0].value;
    var email = document.getElementsByName('email')[0].value;
    var username = document.getElementsByName('username')[0].value;
    var password = document.getElementsByName('password')[0].value;
    var confirmPassword = document.getElementsByName('confirm_password')[0].value;

    var errorMessages = [];

    if (!validateField(firstName, 'error.firstName')) {
        errorMessages.push('[[#{error.firstName}]]');
    }

    if (!validateField(lastName, 'error.lastName')) {
        errorMessages.push('[[#{error.lastName}]]');
    }

    if (!validateField(email, 'error.email')) {
        errorMessages.push('[[#{error.email}]]');
    }

    if (!validateField(username, 'error.username')) {
        errorMessages.push('[[#{error.username}]]');
    }

    if (!validateField(password, 'error.password')) {
        errorMessages.push('[[#{error.password}]]');
    }

    if (password !== confirmPassword) {
        errorMessages.push('[[#{error.passwordMismatch}]]');
    }

    var errorContainer = document.getElementById('errorMessages');
    errorContainer.innerHTML = '';

    if (errorMessages.length > 0) {
        for (var i = 0; i < errorMessages.length; i++) {
            errorContainer.innerHTML += '<p class="error">' + errorMessages[i] + '</p>';
        }
        return false; // блокировать отправку формы, если есть ошибки
    }

    return true;
}

function validateField(value, errorMessageKey) {
    var namePattern = /^[A-Za-zА-Яа-я]{2,15}$/;
    var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    var usernamePattern = /^[A-Za-z0-9]{2,15}$/;
    var passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{4,16}$/;

    if (errorMessageKey === 'error.email') {
        return emailPattern.test(value);
    } else if (errorMessageKey === 'error.username') {
        return usernamePattern.test(value);
    } else if (errorMessageKey === 'error.password') {
        return passwordPattern.test(value);
    } else {
        return namePattern.test(value);
    }
}