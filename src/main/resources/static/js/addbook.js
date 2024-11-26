const addBookIcon = document.getElementById("addBookIcon");
const addBookModal = document.getElementById("addBookModal");
const closeModal = document.getElementById("closeModal");

// Hiển thị modal khi nhấn vào icon
addBookIcon.addEventListener("click", () => {
    addBookModal.style.display = "flex";
});

// Ẩn modal khi nhấn nút Cancel
closeModal.addEventListener("click", () => {
    addBookModal.style.display = "none";
});

// Ngăn form reload trang khi submit
document.getElementById("addBookForm").addEventListener("submit", (e) => {
    e.preventDefault();

    // Lấy dữ liệu từ form
    const bookId = document.getElementById("bookId").value;
    const bookTitle = document.getElementById("bookTitle").value;
    const bookGenres = document.getElementById("bookGenres").value;

    console.log({ bookId, bookTitle, bookGenres });

    // Xử lý logic thêm sách vào danh sách ở đây
    alert("Book added successfully!");

    // Reset form và ẩn modal
    e.target.reset();
    addBookModal.style.display = "none";
});