document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const nameInput = document.getElementById('name');
    const productSelect = document.getElementById('product');
    const priceInput = document.getElementById('price');
    const periodDaysInput = document.getElementById('periodDays');

    form.addEventListener('submit', function(event) {
        let isValid = true;

        if (!nameInput.value.trim()) {
            nameInput.classList.add('is-invalid');
            isValid = false;
        } else {
            nameInput.classList.remove('is-invalid');
        }

        if (!productSelect.value) {
            productSelect.classList.add('is-invalid');
            isValid = false;
        } else {
            productSelect.classList.remove('is-invalid');
        }

        if (!priceInput.value || parseFloat(priceInput.value) < 0) {
            priceInput.classList.add('is-invalid');
            isValid = false;
        } else {
            priceInput.classList.remove('is-invalid');
        }

        if (!periodDaysInput.value || parseInt(periodDaysInput.value) < 1) {
            periodDaysInput.classList.add('is-invalid');
            isValid = false;
        } else {
            periodDaysInput.classList.remove('is-invalid');
        }

        if (!isValid) {
            event.preventDefault();
        }
    });

    [nameInput, productSelect, priceInput, periodDaysInput].forEach(input => {
        input.addEventListener('input', function() {
            this.classList.remove('is-invalid');
        });
    });
}); 