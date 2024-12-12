document.addEventListener('DOMContentLoaded', () => {
    const deleteIcons = document.querySelectorAll('.delete-icon');

    deleteIcons.forEach(icon => {
        icon.addEventListener('click', () => {
            const bookId = icon.getAttribute('data-id');
            if (confirm('Are you sure you want to delete this book?')) {
                fetch(`/admin/book/delete/${bookId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (response.ok) {
                        alert('Book deleted successfully.');
                        location.reload();
                    } else {
                        alert('Failed to delete book.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while deleting the book.');
                });
            }
        });
    });
});