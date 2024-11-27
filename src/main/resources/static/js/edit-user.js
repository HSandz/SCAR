// Get the necessary elements
const editIcons = document.querySelectorAll('.edit-icon');
const editUserModal = document.getElementById('editUser');
const closeModal = document.getElementById('closeModal');

// Show modal when clicking on edit icon
editIcons.forEach((icon) => {
    icon.addEventListener('click', () => {
        console.log('Edit icon clicked');
        editUserModal.style.display = 'flex';
    });
});

// Hide modal when clicking on Cancel button
closeModal.addEventListener('click', () => {
    console.log('Close button clicked');
    editUserModal.style.display = 'none';
});

// Prevent form from reloading the page when submitted
document.getElementById('user-list').addEventListener('submit', (e) => {
    e.preventDefault();

    // Get data from the form
    const userId = document.getElementById('userId').value;
    const username = document.getElementById('Username').value;
    const displayName = document.getElementById('Display-name').value;
    const role = document.getElementById('Role').value;

    console.log({ userId, username, displayName, role });

    // Handle user information update logic here
    alert('User details updated successfully!');

    // Reset form and hide modal
    e.target.reset();
    editUserModal.style.display = 'none';
});