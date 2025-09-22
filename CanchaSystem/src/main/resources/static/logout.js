
(function () {
    document.addEventListener("DOMContentLoaded", function () {
        const logoutBtn = document.getElementById("logoutBtn");

        if (logoutBtn) {
            logoutBtn.style.display = "inline-block";
            logoutBtn.addEventListener("click", () => {
                fetch("/logout", {
                    method: "POST",
                    credentials: "include",
                    headers: {
                        "X-Requested-With": "XMLHttpRequest"
                    }
                }).then(response => {
                    window.location.href = "/login.html";
                }).catch(err => {
                    console.error("Error al cerrar sesión", err);
                    alert("Error al cerrar sesión");
                });
            });
        }
    });
})();
