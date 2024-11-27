const addBookIcon = document.getElementById("addBookIcon");
const addBookModal = document.getElementById("addBookModal");
const closeModal = document.getElementById("closeModal");

addBookIcon.addEventListener("click", () => {
    addBookModal.style.display = "flex";
});

closeModal.addEventListener("click", () => {
    addBookModal.style.display = "none";
});

document.getElementById("addBookForm").addEventListener("submit", (e) => {
    e.preventDefault();

    const bookId = document.getElementById("bookId").value;
    const bookTitle = document.getElementById("bookTitle").value;
    const bookGenres = document.getElementById("bookGenres").value;

    console.log({ bookId, bookTitle, bookGenres });

    alert("Book added successfully!");

    e.target.reset();
    addBookModal.style.display = "none";
});
