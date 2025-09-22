document.addEventListener("DOMContentLoaded", () => {
  cargarMarcas();

  document.getElementById("filtroNombre").addEventListener("input", cargarMarcas);
  document.getElementById("filtroEstado").addEventListener("change", cargarMarcas);
});

async function cargarMarcas() {
  const lista = document.getElementById("listaMarcas");
  lista.innerHTML = "<li>Cargando...</li>";

  try {
    const [marcasRes, earningsRes] = await Promise.all([
      fetch("http://localhost:8080/canchaBrand/findall"),
      fetch("http://localhost:8080/stats/owner/0/brand/lifetime/all")
    ]);

    const marcas = await marcasRes.json();
    const allEarnings = await earningsRes.json();

    const nombre = document.getElementById("filtroNombre").value.toLowerCase();
    const estado = document.getElementById("filtroEstado").value;

    const filtradas = marcas.filter(m =>
      (!nombre || m.brandName.toLowerCase().includes(nombre)) &&
      (estado === "" ||
        (estado === "activa" && m.active) ||
        (estado === "inactiva" && !m.active))
    );

    if (filtradas.length === 0) {
      lista.innerHTML = "<li>No se encontraron marcas.</li>";
      return;
    }

    lista.innerHTML = "";

    filtradas.forEach(m => {
      const stat = allEarnings.find(e => e.brandId === m.id);
      const total = (stat?.totalEarnings || 0).toFixed(2);

      const li = document.createElement("li");
      li.innerHTML = `
        <input type="text" value="${m.brandName}" id="brandName-${m.id}">
        <label><input type="checkbox" id="active-${m.id}" ${m.active ? "checked" : ""}> Activa</label>

        <div class="actions-bar">
          <div class="left-buttons">
            <button onclick="guardarMarca(${m.id})">Guardar</button>
            <button onclick="eliminarMarca(${m.id})" class="btn-eliminar">Eliminar</button>
          </div>
          <div class="right-earnings">
            <span class="earnings-label" id="earnings-${m.id}">$${total} (Total)</span>
            <div class="earnings-buttons">
              <button onclick="updateEarnings(${m.id}, ${m.owner.id}, 'daily')">Diario</button>
              <button onclick="updateEarnings(${m.id}, ${m.owner.id}, 'weekly')">Semanal</button>
              <button onclick="updateEarnings(${m.id}, ${m.owner.id}, 'monthly')">Mensual</button>
              <button onclick="updateEarnings(${m.id}, ${m.owner.id}, 'yearly')">Anual</button>
            </div>
          </div>
        </div>

        <hr>
      `;
      lista.appendChild(li);
    });
  } catch (err) {
    console.error(err);
    lista.innerHTML = "<li>Error al cargar marcas.</li>";
  }
}

async function updateEarnings(brandId, ownerId, timeframe) {
  try {
    const res = await fetch(`http://localhost:8080/stats/owner/${ownerId}/brand/${timeframe}`);
    const data = await res.json();

    const brandEntries = data.filter(e => e.brandId === brandId);
    const total = brandEntries.reduce((sum, e) => sum + (e.totalEarnings || 0), 0);

    const label = document.getElementById(`earnings-${brandId}`);
    const timeframeMap = {
      daily: "Hoy",
      weekly: "Semana",
      monthly: "Mes",
      yearly: "Año"
    };

    label.textContent = `$${total.toFixed(2)} (${timeframeMap[timeframe]})`;
  } catch (error) {
    console.error("Error al obtener estadísticas:", error);
  }
}

async function guardarMarca(id) {
  const marcaEditada = {
    id,
    brandName: document.getElementById(`brandName-${id}`).value,
    active: document.getElementById(`active-${id}`).checked
  };

  try {
    const res = await fetch("http://localhost:8080/canchaBrand/update", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(marcaEditada)
    });

    if (res.ok) {
      alert("Marca actualizada correctamente");
      cargarMarcas();
    } else {
      const errorText = await res.text();
      alert("Error al actualizar la marca:\n" + errorText);
    }
  } catch (e) {
    console.error(e);
    alert("Error de red");
  }
}

async function eliminarMarca(id) {
  if (!confirm("¿Seguro que querés eliminar esta marca?")) return;

  try {
    const res = await fetch(`http://localhost:8080/canchaBrand/deleteCanchaBrand/${id}`, {
      method: "DELETE"
    });

    const data = await res.json();

    if (res.ok) {
      alert(data.message);
      cargarMarcas();
    } else {
      alert("Error al eliminar la marca: " + data.message);
    }
  } catch (e) {
    console.error(e);
    alert("Error de red");
  }
}
