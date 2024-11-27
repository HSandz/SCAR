document.addEventListener("DOMContentLoaded", function () {
    const bookItems = document.querySelectorAll(".book-item");

    bookItems.forEach((item, index) => {
        const numberSpan = item.querySelector(".book-number");
        if (numberSpan) {
            numberSpan.textContent = index + 1;
        }
    });

    const scrollContainer = document.querySelector('.scroll-content');
    const scrollButtonLeft = document.querySelector('.scroll-btn.left');
    const scrollButtonRight = document.querySelector('.scroll-btn.right');

    scrollButtonLeft.addEventListener('click', () => {
        scrollContainer.scrollBy({left: -200, behavior: 'smooth'});
    });

    scrollButtonRight.addEventListener('click', () => {
        scrollContainer.scrollBy({left: 200, behavior: 'smooth'});
    });

    const borrowButtons = document.querySelectorAll(".borrow-btn");
    const favouriteButtons = document.querySelectorAll(".bx-heart");

    borrowButtons.forEach(button => {
        button.addEventListener("click", function () {
            const bookId = this.closest(".book-item").getAttribute("data-book-id");

            fetch(`/books/borrow/${bookId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(response => response.text())
                .then(data => {
                    if (data === "Book borrowed successfully") {
                        alert("Book borrowed successfully");
                    } else {
                        alert("Failed to borrow book");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("An error occurred while borrowing the book");
                });
        });
    });

    favouriteButtons.forEach(button => {
        button.addEventListener("click", function () {
            const bookId = this.closest(".book-item").getAttribute("data-book-id");

            fetch(`/books/add-favourite/${bookId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(response => response.text())
                .then(data => {
                    if (data === "Book added to favourites") {
                        alert("Book added to favourites");
                    } else {
                        alert("Failed to add favourite");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("An error occurred while adding the book to favourites");
                });
        });
    });

    const bookImages = document.querySelectorAll(".book-item img");

    bookImages.forEach(img => {
        img.addEventListener("click", function () {
            const bookId = this.getAttribute("data-book-id");
            window.location.href = `/books/${bookId}`;
        });
    });
});