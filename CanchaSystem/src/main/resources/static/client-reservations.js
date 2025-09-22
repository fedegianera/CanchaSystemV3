document.addEventListener("DOMContentLoaded", async () => {
  const pendientes = document.getElementById("reservasPendientes");
  const finalizadas = document.getElementById("reservasFinalizadas");

  pendientes.innerHTML = "<li>Cargando pendientes...</li>";
  finalizadas.innerHTML = "<li>Cargando...</li>";

  try {
    const res = await fetch("http://localhost:8080/reservation/findReservationsByClient");
    const reservas = await res.json();

    pendientes.innerHTML = "";
    finalizadas.innerHTML = "";

    if (reservas.length === 0) {
      pendientes.innerHTML = "<li>No hay reservas registradas.</li>";
      finalizadas.innerHTML = "";
      return;
    }

    reservas.forEach(r => {
      const li = document.createElement("li");

      const fechaPartido = new Date(r.matchDate).toLocaleString();

      let estadoHtml = '';
      switch (r.status) {
        case "CANCELED":
          estadoHtml = `<span class="status canceled">Cancelada</span>`;
          break;
        case "COMPLETED":
          estadoHtml = `<span class="status completed">Completada</span>`;
          break;
        case "PENDING":
          estadoHtml = `<span class="status pending">Pendiente</span>`;
          break;
        default:
          estadoHtml = `<span class="status">${r.status}</span>`;
      }

      li.innerHTML = `
        <strong>${r.cancha.name}</strong> - ${r.cancha.brand.brandName} <br>
        Fecha partido: ${fechaPartido} <br>
        Depósito: $${r.deposit} <br>
        Estado: ${estadoHtml}
      `;

      if (r.status === "PENDING") {
        const btn = document.createElement("button");
        btn.textContent = "Cancelar";
        btn.className = "cancel-btn";
        btn.onclick = () => cancelarReserva(r.id);
        li.appendChild(document.createElement("br"));
        li.appendChild(btn);
        pendientes.appendChild(li);
      } else {
        finalizadas.appendChild(li);
      }
    });

    if (pendientes.innerHTML.trim() === "") {
      pendientes.innerHTML = "<li>No hay reservas pendientes.</li>";
    }
    if (finalizadas.innerHTML.trim() === "") {
      finalizadas.innerHTML = "<li>No hay reservas finalizadas o canceladas.</li>";
    }

  } catch (err) {
    pendientes.innerHTML = "<li>Error al cargar reservas.</li>";
    finalizadas.innerHTML = "";
    console.error(err);
  }
});

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
