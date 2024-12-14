document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const nameInput = document.getElementById('name');

    form.addEventListener('submit', function(event) {
        let isValid = true;

        if (!nameInput.value.trim()) {
            nameInput.classList.add('is-invalid');
            isValid = false;
        } else {
            nameInput.classList.remove('is-invalid');
        }

        if (!isValid) {
            event.preventDefault();
        }
    });

    nameInput.addEventListener('input', function() {
        this.classList.remove('is-invalid');
    });
}); 