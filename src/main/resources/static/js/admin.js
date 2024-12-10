// Инициализация всех тултипов
var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
});

// Инициализация DataTables
$(document).ready(function() {
    $('.datatable').DataTable({
        language: {
            url: '/js/dataTables.russian.json'
        },
        pageLength: 10,
        responsive: true
    });
});

// Подтверждение удаления
function confirmDelete(event, message) {
    if (!confirm(message || 'Вы уверены, что хотите удалить этот элемент?')) {
        event.preventDefault();
    }
}

// Предпросмотр изображения
function previewImage(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            $('#imagePreview').attr('src', e.target.result);
        }
        reader.readAsDataURL(input.files[0]);
    }
} 