document.addEventListener("DOMContentLoaded", async () => {
        const lista = document.getElementById("listaReseñas");
        lista.innerHTML = "<li>Cargando reseñas...</li>";

        try {
            const res = await fetch("http://localhost:8080/review/findReviewsByClient");
            const resenas = await res.json();

            if (resenas.length === 0) {
                lista.innerHTML = "<li>Aún no hay reseñas.</li>";
                return;
            }

            lista.innerHTML = "";
            resenas.forEach(r => {
                const li = document.createElement("li");

                li.innerHTML = `
                    <strong>${r.cancha.name}</strong> - ${r.cancha.brand.brandName}<br>
                    <div id="stars-${r.id}" data-rating="${Math.round(r.rating)}">
                        ${renderStars(Math.round(r.rating), r.id, false)}
                    </div>
                    <textarea id="msg-${r.id}" class="readonly" readonly rows="3" cols="40">${r.message || ""}</textarea><br>
                    <button id="btn-edit-${r.id}" onclick="activarEdicion(${r.id})">Editar</button>
                    <button id="btn-save-${r.id}" onclick="guardarCambios(${r.id})" style="display:none;">Guardar</button>
                    <button onclick="eliminarResena(${r.id})" class="btn-eliminar">Eliminar</button>
                    <hr>
                `;
                lista.appendChild(li);
            });
        } catch (err) {
            lista.innerHTML = "<li>Error al cargar las reseñas.</li>";
            console.error(err);
        }
    });

    function renderStars(valor, reviewId, editable) {
        let html = "";
        for (let i = 1; i <= 5; i++) {
            html += `<span class="star ${i <= valor ? "selected" : ""}" ${editable ? `onclick="seleccionarEstrellas(${reviewId}, ${i})"` : ''}>★</span>`;
        }
        return html;
    }

    function activarEdicion(id) {
        const textarea = document.getElementById(`msg-${id}`);
        textarea.readOnly = false;
        textarea.classList.remove("readonly");
        textarea.classList.add("editable");
        const ratingDiv = document.getElementById(`stars-${id}`);
        const currentRating = parseInt(ratingDiv.dataset.rating || 0);
        ratingDiv.innerHTML = renderStars(currentRating, id, true);

        document.getElementById(`btn-edit-${id}`).style.display = "none";
        document.getElementById(`btn-save-${id}`).style.display = "inline-block";
    }

    function seleccionarEstrellas(reviewId, valor) {
        const container = document.querySelector(`#stars-${reviewId}`);
        container.dataset.rating = valor;
        container.innerHTML = renderStars(valor, reviewId, true);
    }

    async function guardarCambios(id) {
        const rating = parseInt(document.getElementById(`stars-${id}`).dataset.rating || 0);
        const message = document.getElementById(`msg-${id}`).value;

        if (rating < 1 || rating > 5) {
            alert("Seleccioná un rating válido.");
            return;
        }

        const reviewActualizada = {
            id,
            rating,
            message
        };

        try {
            const res = await fetch("http://localhost:8080/review/update", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(reviewActualizada)
            });

            if (res.ok) {
                alert("Reseña actualizada 🎉");

                const textarea = document.getElementById(`msg-${id}`);
                textarea.readOnly = true;
                textarea.classList.remove("editable");
                textarea.classList.add("readonly");

                const div = document.getElementById(`stars-${id}`);
                div.innerHTML = renderStars(rating, id, false);

                document.getElementById(`btn-edit-${id}`).style.display = "inline-block";
                document.getElementById(`btn-save-${id}`).style.display = "none";
            } else {
                alert("Error al actualizar la reseña.");
            }
        } catch (err) {
            console.error(err);
            alert("Fallo al guardar los cambios.");
        }
    }

    async function eliminarResena(id) {
        if (!confirm("¿Seguro que querés eliminar esta reseña?")) return;

        try {
            const res = await fetch(`http://localhost:8080/review/delete/${id}`, {
                method: "DELETE"
            });

            if (res.ok) {
                alert("Reseña eliminada correctamente 🗑️");
                location.reload();
            } else {
                alert("No se pudo eliminar.");
            }
        } catch (e) {
            console.error(e);
            alert("Error de red al eliminar reseña.");
        }
    }