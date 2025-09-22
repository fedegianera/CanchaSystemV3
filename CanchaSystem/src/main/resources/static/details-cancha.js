const canchaId = new URLSearchParams(window.location.search).get("canchaId");
let clientId = null;

document.addEventListener("DOMContentLoaded", init);

async function init() {
  if (!canchaId) {
    document.body.innerHTML = "<h2>Error: Cancha no especificada.</h2>";
    return;
  }

  try {
    const resCliente = await fetch("http://localhost:8080/client/me");
    const cliente = await resCliente.json();
    clientId = cliente.id;

    await verificarSiYaReseno();

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

    await cargarResenas();
  } catch (e) {
    console.error(e);
    document.body.innerHTML = "<h2>Error al cargar los datos.</h2>";
  }
}

async function verificarSiYaReseno() {
  try {
    const res = await fetch(`http://localhost:8080/review/clientReviewExists?canchaId=${canchaId}&clientId=${clientId}`);
    const yaReseno = await res.json();

    if (yaReseno) {
      document.getElementById("tituloResena").style.display = "none";
      document.getElementById("seccionResena").style.display = "none";
    } else {
      prepararEstrellas();
    }
  } catch (e) {
    console.error("Error al verificar si ya reseñó:", e);
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

function seleccionarEstrellas(valor) {
  document.getElementById("rating").value = valor.toFixed(1);
  document.querySelectorAll("#stars .star").forEach((s, idx) => {
    s.classList.toggle("selected", idx < valor);
  });
}

async function enviarResena() {
  const rating = parseFloat(document.getElementById("rating").value);
  const message = document.getElementById("comentario").value;

  if (isNaN(rating) || rating < 1 || rating > 5) {
    alert("Seleccioná una valoración entre 1 y 5.");
    return;
  }

  const review = {
    rating,
    message: message || null,
    client: { id: clientId },
    cancha: { id: canchaId }
  };

  try {
    const res = await fetch("http://localhost:8080/review/insert", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(review)
    });
    if (res.ok) {
      alert("¡Reseña enviada!");
      location.reload();
    } else {
      alert("Error al enviar reseña.");
    }
  } catch (e) {
    console.error(e);
    alert("Error de red.");
  }
}

async function cargarResenas() {
  try {
    const res = await fetch(`http://localhost:8080/review/findReviewsByCanchaId/${canchaId}`);
    const resenas = await res.json();
    const lista = document.getElementById("listaResenas");

    if (resenas.length === 0) {
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

document.getElementById("fechaInput").addEventListener("change", async () => {
  const fecha = document.getElementById("fechaInput").value;
  const horarioSelect = document.getElementById("horarioSelect");
  horarioSelect.innerHTML = "<option>Cargando horarios...</option>";

  try {
    const res = await fetch(`http://localhost:8080/reservation/getAvailableHours/${canchaId}/${fecha}`);
    if (res.status === 204) {
      horarioSelect.innerHTML = "<option>No hay horarios disponibles</option>";
      return;
    }

    const horas = await res.json();
    horarioSelect.innerHTML = "";
    horas.forEach(h => {
      const opt = document.createElement("option");
      opt.value = h;
      opt.textContent = h;
      horarioSelect.appendChild(opt);
    });
  } catch (e) {
    horarioSelect.innerHTML = "<option>Error al cargar horarios</option>";
    console.error(e);
  }
});

document.getElementById("formReserva").addEventListener("submit", async (e) => {
  e.preventDefault();
  const fechaHora = `${document.getElementById("fechaInput").value}T${document.getElementById("horarioSelect").value}`;
  const body = {
    cancha: { id: canchaId },
    matchDate: fechaHora
  };

  try {
    const res = await fetch("http://localhost:8080/reservation/insert", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });

    if (res.ok) {
      alert("¡Reserva confirmada!");
      location.reload();
    } else {
      alert("Error al reservar.");
    }
  } catch (e) {
    console.error(e);
    alert("Error de red.");
  }
});
