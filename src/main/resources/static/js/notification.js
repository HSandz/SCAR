document.getElementById("bellIcon").addEventListener("click", function () {
    const mainContent = document.getElementById("main-content");
    const rightScreen = document.getElementById("rightScreen");

    // Toggle classes to apply the effect
    rightScreen.classList.toggle("active");
    mainContent.classList.toggle("shrink");
});