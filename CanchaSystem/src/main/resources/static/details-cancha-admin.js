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

    await cargarResenas();
    await cargarReservas();
  } catch (e) {
    console.error(e);
    document.body.innerHTML = "<h2>Error al cargar los datos.</h2>";
  }
}

async function cargarResenas() {
  try {
    const res = await fetch(`http://localhost:8080/review/findReviewsByCanchaIdAdmin/${canchaId}`);
    const resenas = await res.json();
    const lista = document.getElementById("listaResenas");
    lista.innerHTML = "";

    if (resenas.length === 0) {
      lista.innerHTML = "<li>No hay reseñas aún.</li>";
      return;
    }

    resenas.forEach(r => {
      const li = document.createElement("li");

      const estado = r.active ? "Activa" : "<span style='color: red;'>ELIMINADA</span>";
      const puedeEliminar = r.active
        ? `<button onclick="eliminarResena(${r.id})" class="btn-eliminar">Eliminar</button>`
        : "";

      li.innerHTML = `
        <strong>${r.client.name}</strong> (${estado})<br>
        ${renderStars(Math.round(r.rating))}<br>
        ${r.message || "<i>Sin comentario</i>"}<br>
        ${puedeEliminar}
        <hr>`;
      lista.appendChild(li);
    });
  } catch (e) {
    document.getElementById("listaResenas").innerHTML = "<li>Error al cargar reseñas.</li>";
    console.error(e);
  }
}


async function eliminarResena(id) {
  if (!confirm("¿Seguro que querés eliminar esta reseña?")) return;

  try {
    await fetch(`http://localhost:8080/review/delete/${id}`, {
      method: "DELETE",
      credentials: "include"
    });
    alert("Reseña eliminada");
    await cargarResenas();
  } catch (e) {
    console.error("Error al eliminar reseña:", e);
    alert("No se pudo eliminar la reseña.");
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
    lista.innerHTML = "";

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

      const estadoReserva = r.status == "CANCELED"
        ? `<span style="color:#cc0000;">CANCELADA</span>`
        : r.status == "COMPLETED"
          ? `<span style="color:#00ff77;">CONFIRMADA</span>`
          : `<span style="color:blue;">PENDIENTE</span>`;

      const puedeCancelar = r.status === "PENDING"
        ? `<button onclick="cancelarReserva(${r.id})">Cancelar</button>`
        : "";

      li.innerHTML = `
        <strong>${r.client.username}</strong><br>
        Fecha: ${fechaFormateada}<br>
        Depósito: ${depositoFormateado}<br>
        Estado: ${estadoReserva}<br>
        ${puedeCancelar}
        <hr>
      `;
      lista.appendChild(li);
    });


  } catch (e) {
    document.getElementById("listaReservas").innerHTML = "<li>Error al cargar reservas.</li>";
    console.error(e);
  }
}

  async function cancelarReserva(id) {
    if (!confirm("¿Confirmás cancelar esta reserva?")) return;

    try {
      const resActual = await fetch(`http://localhost:8080/reservation/${id}`);
      const reserva = await resActual.json();

      reserva.status = "CANCELED";

      const res = await fetch("http://localhost:8080/reservation/update", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reserva)
      });

      if (res.ok) {
        alert("Reserva cancelada ✅");
        location.reload();
      } else {
        alert("No se pudo cancelar.");
      }
    } catch (err) {
      console.error(err);
      alert("Error al cancelar la reserva.");
    }
  }
