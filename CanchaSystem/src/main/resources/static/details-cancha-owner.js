const canchaId = new URLSearchParams(window.location.search).get("canchaId");


  document.addEventListener("DOMContentLoaded", init);

  async function init() {
    if (!canchaId) {
      document.body.innerHTML = "<h2>Error: Cancha no especificada.</h2>";
      return;
    }

    try {
      const resCancha = await fetch(`http://localhost:8080/cancha/findCanchaById/${canchaId}`);
      const cancha = await resCancha.json();

      document.getElementById("detalleCancha").innerHTML = `
        <strong>${cancha.name}</strong><br>
        Dirección: ${cancha.address}<br>
        Tipo: ${cancha.canchaType}<br>
        Marca: ${cancha.brand.brandName}<br>
        Precio: $${cancha.totalAmount}<br>
        Horario: ${cancha.openingHour} - ${cancha.closingHour}<br>
        Ducha: ${cancha.canShower ? "Sí" : "No"}<br>
        Techo: ${cancha.hasRoof ? "Sí" : "No"}<br>
      `;

      try {
        await cargarResenas();
      } catch (error) {
        console.error("Error en cargarResenas:", error);
      }
    } catch (e) {
      console.error(e);
      document.body.innerHTML = "<h2>Error al cargar los datos.</h2>";
    }
  }

  function prepararEstrellas() {
    const starsDiv = document.getElementById("stars");
    for (let i = 1; i <= 5; i++) {
      const span = document.createElement("span");
      span.textContent = "★";
      span.classList.add("star");
      span.dataset.value = i;
      span.onclick = () => seleccionarEstrellas(i);
      starsDiv.appendChild(span);
    }
  }

  async function cargarResenas() {
    try {
      const res = await fetch(`http://localhost:8080/review/findReviewsByCanchaId/${canchaId}`);
      const resenas = await res.json();
      const lista = document.getElementById("listaResenas");

      if (resenas.length == 0) {
        lista.innerHTML = "<li>No hay reseñas aún.</li>";
        return;
      }

      resenas.forEach(r => {
        const li = document.createElement("li");
        li.innerHTML = `
          <strong>${r.client.name}</strong><br>
          ${renderStars(Math.round(r.rating))}<br>
          ${r.message || "<i>Sin comentario</i>"}
          <hr>`;
        lista.appendChild(li);
      });
    } catch (e) {
      document.getElementById("listaResenas").innerHTML = "<li>Error al cargar reseñas.</li>";
      console.error(e);
    }
  }

  function renderStars(valor) {
    let html = "";
    for (let i = 1; i <= 5; i++) {
      html += `<span class="star ${i <= valor ? "selected" : ""}">★</span>`;
    }
    return html;

  }

  async function cargarReservas() {
    try {
        const res = await fetch(`http://localhost:8080/reservation/getReservationsByCanchaId/${canchaId}`);
            const reservations = await res.json();
            const lista = document.getElementById("listaReservas");

            if (reservations.length === 0) {
              lista.innerHTML = "<li>No hay reservas aún.</li>";
              return;
            }

            reservations.forEach(r => {
              const li = document.createElement("li");

              const fecha = new Date(r.matchDate);
              const fechaFormateada = fecha.toLocaleString("es-AR", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
              });

              const depositoFormateado = new Intl.NumberFormat("es-AR", {
                style: "currency",
                currency: "ARS",
                minimumFractionDigits: 2,
              }).format(r.deposit);

              li.innerHTML = `
                <strong>${r.client.username}</strong><br>
                ${fechaFormateada}<br>
                ${depositoFormateado}
                <hr>
              `;
              lista.appendChild(li);
            });
    } catch(e) {
        document.getElementById("listaReservas").innerHTML = "<li>Error al cargar reservas</li>";
        console.error(e);
    }
  }
