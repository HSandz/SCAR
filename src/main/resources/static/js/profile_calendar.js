document.querySelectorAll('.day').forEach(day => {
    day.addEventListener('click', () => {
        day.classList.toggle('active');
    });
});