document.addEventListener('DOMContentLoaded', () => {

    const editIcons = document.querySelectorAll('.edit-icon');
    const deleteIcons = document.querySelectorAll('.delete-icon');
    const editUserModal = document.getElementById('editUser');
    const closeModal = document.getElementById('closeModal');

    editIcons.forEach((icon) => {
        icon.addEventListener('click', () => {
            const userId = icon.getAttribute('data-id');
            const username = icon.getAttribute('data-username');
            const displayName = icon.getAttribute('data-displayname');
            const role = icon.getAttribute('data-role');

            document.getElementById('userId').value = userId;
            document.getElementById('Username').value = username;
            document.getElementById('Display-name').value = displayName;
            document.getElementById('Role').value = role;

            editUserModal.style.display = 'flex';
        });
    });

    if (closeModal) {
        closeModal.addEventListener('click', () => {
            editUserModal.style.display = 'none';
        });
    } else {
        console.error('Cancel button (closeModal) not found.');
    }

    deleteIcons.forEach((icon) => {
        icon.addEventListener('click', () => {
            const userId = icon.getAttribute('data-id');
            if (userId && confirm('Are you sure you want to delete this user?')) {
                fetch(`/admin/user/delete/${userId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                })
                .then(response => {
                    if (response.ok) {
                        alert('User deleted successfully!');
                        location.reload();
                    } else {
                        alert('Failed to delete user.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to delete user.');
                });
            } else {
                console.error('User ID is null or undefined.');
            }
        });
    });

    const userForm = document.getElementById('user-list');
    userForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const userId = document.getElementById('userId').value;
        const username = document.getElementById('Username').value;
        const displayName = document.getElementById('Display-name').value;
        const role = document.getElementById('Role').value;

        fetch('/admin/user/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                id: userId,
                username: username,
                displayName: displayName,
                role: role
            })
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                } else {
                    return response.text().then(errMessage => {
                        throw new Error(errMessage);
                    });
                }
            })
            .then(message => {
                alert(message);
                location.reload();
            })
            .catch(error => {
                console.error('Error:', error.message);
                alert(`An error occurred: ${error.message}`);
            });

        document.getElementById('editUser').style.display = 'none';
    });

});