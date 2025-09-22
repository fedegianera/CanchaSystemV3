async function fetchRequests() {
  try {
    const response = await fetch('/request/getAllPending');
    const requests = await response.json();

    const tbody = document.querySelector('#requestTable tbody');
    tbody.innerHTML = '';

    if (requests.length == 0) {
      document.getElementById("message").textContent = "No hay solicitudes pendientes.";
      return;
    } else {
      document.getElementById("message").textContent = "";
    }

    requests.forEach(req => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${req.client.name} ${req.client.lastName || ''}</td>
        <td>${new Date(req.requestDate).toLocaleString()}</td>
        <td>
          <button class="accept" onclick="handleAction(${req.id}, 'approve')">Aceptar</button>
          <button class="reject" onclick="handleAction(${req.id}, 'deny')" class="btn-eliminar">Rechazar</button>
        </td>
      `;
      tbody.appendChild(row);
    });

  } catch (error) {
    console.error("Error al cargar solicitudes:", error);
    document.getElementById("message").textContent = "Error cargando solicitudes.";
  }
}

async function handleAction(requestId, action) {
  const endpoint = `/admin/${action}Request/${requestId}`;
  try {
    const response = await fetch(endpoint, {
      method: 'PUT'
    });

    if (response.ok) {
      const message = action === 'approve' ? 'Solicitud aprobada' : 'Solicitud rechazada';
      alert(message);
      fetchRequests();
    } else {
      const data = await response.json();
      alert("Error: " + (data.message || response.status));
    }
  } catch (error) {
    alert("Error de red al procesar la solicitud.");
  }
}

fetchRequests();
