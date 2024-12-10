document.addEventListener('DOMContentLoaded', function() {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const progressBar = document.querySelector('.upload-progress-bar');
    const progressContainer = document.querySelector('.upload-progress');

    // Drag & Drop
    dropZone.addEventListener('click', () => fileInput.click());
    
    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.classList.add('dragover');
    });

    dropZone.addEventListener('dragleave', () => {
        dropZone.classList.remove('dragover');
    });

    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.classList.remove('dragover');
        fileInput.files = e.dataTransfer.files;
        updateFileName(e.dataTransfer.files[0].name);
    });

    // Обработка выбора файла
    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            updateFileName(e.target.files[0].name);
        }
    });

    // Обновление прогресса загрузки
    function updateProgress(progress) {
        progressContainer.classList.remove('d-none');
        progressBar.style.width = progress + '%';
    }

    // Обновление имени файла
    function updateFileName(name) {
        dropZone.querySelector('p').textContent = name;
    }

    // Интеграция с формой
    document.querySelector('form').addEventListener('submit', function(e) {
        const file = fileInput.files[0];
        if (!file) return;

        // Имитация прогресса загрузки
        let progress = 0;
        const interval = setInterval(() => {
            progress += 10;
            updateProgress(progress);
            if (progress >= 100) clearInterval(interval);
        }, 200);
    });
}); 