document.addEventListener("DOMContentLoaded", () => {
        cargarCanchas();

        document.getElementById("filtroNombre").addEventListener("input", cargarCanchas);
        document.getElementById("filtroTipo").addEventListener("change", cargarCanchas);
        document.getElementById("filtroTecho").addEventListener("change", cargarCanchas);
        document.getElementById("filtroDucha").addEventListener("change", cargarCanchas);
    });

    async function cargarCanchas() {
        const lista = document.getElementById("listaCanchas");
        lista.innerHTML = "<li>Cargando...</li>";

        try {
            const [resCanchas, resEarnings] = await Promise.all([
              fetch("http://localhost:8080/cancha/findall"),
              fetch("http://localhost:8080/stats/owner/0/cancha/lifetime/all")
            ]);

            const canchas = await resCanchas.json();
            const earnings = await resEarnings.json();


            const nombre = document.getElementById("filtroNombre").value.toLowerCase();
            const tipo = document.getElementById("filtroTipo").value;
            const techo = document.getElementById("filtroTecho").checked;
            const ducha = document.getElementById("filtroDucha").checked;

            const filtradas = canchas.filter(c =>
                (!nombre || c.name.toLowerCase().includes(nombre)) &&
                (!tipo || c.canchaType === tipo) &&
                (!techo || c.hasRoof) &&
                (!ducha || c.canShower)
            );

            if (filtradas.length === 0) {
                lista.innerHTML = "<li>No se encontraron canchas.</li>";
                return;
            }

            lista.innerHTML = "";
            filtradas.forEach(c => {
                const stat = earnings.find(e => e.canchaId === c.id);
                const total = (stat?.totalEarnings || 0).toFixed(2);
                const li = document.createElement("li");
                li.innerHTML = `
                  <input type="text" value="${c.name}" id="name-${c.id}">
                  <input type="text" value="${c.address}" id="address-${c.id}">
                  <select id="type-${c.id}">
                      <option value="FUTBOL_5" ${c.canchaType === "FUTBOL_5" ? "selected" : ""}>Fútbol 5</option>
                      <option value="FUTBOL_7" ${c.canchaType === "FUTBOL_7" ? "selected" : ""}>Fútbol 7</option>
                      <option value="FUTBOL_11" ${c.canchaType === "FUTBOL_11" ? "selected" : ""}>Fútbol 11</option>
                  </select>
                  <input type="number" value="${c.totalAmount}" id="amount-${c.id}">
                  <input type="time" value="${c.openingHour}" id="open-${c.id}">
                  <input type="time" value="${c.closingHour}" id="close-${c.id}">
                  <label><input type="checkbox" id="roof-${c.id}" ${c.hasRoof ? "checked" : ""}> Techo</label>
                  <label><input type="checkbox" id="shower-${c.id}" ${c.canShower ? "checked" : ""}> Ducha</label>

                  <div class="cancha-action-row">
                    <div class="left-buttons">
                      <button onclick="guardarCambios(${c.id})">Guardar</button>
                      <button onclick="eliminarCancha(${c.id})" class="btn-eliminar">Eliminar</button>
                      <button onclick="location.href='details-cancha-admin.html?canchaId=${c.id}'">Ver Detalles</button>
                    </div>
                    <div class="right-earnings">
                      <span class="earnings-label" id="earnings-${c.id}">$${total} (Total)</span>
                      <div class="earnings-buttons">
                        <button onclick="updateCanchaEarnings(${c.id}, ${c.brand.owner.id}, 'daily')">Diario</button>
                        <button onclick="updateCanchaEarnings(${c.id}, ${c.brand.owner.id}, 'weekly')">Semanal</button>
                        <button onclick="updateCanchaEarnings(${c.id}, ${c.brand.owner.id}, 'monthly')">Mensual</button>
                        <button onclick="updateCanchaEarnings(${c.id}, ${c.brand.owner.id}, 'yearly')">Anual</button>
                      </div>
                    </div>
                  </div>
                  <hr>
                `;

                lista.appendChild(li);
            });

        } catch (err) {
            console.error(err);
            lista.innerHTML = "<li>Error al cargar canchas.</li>";
        }
    }

    async function guardarCambios(id) {
        const canchaEditada = {
            id,
            name: document.getElementById(`name-${id}`).value,
            address: document.getElementById(`address-${id}`).value,
            canchaType: document.getElementById(`type-${id}`).value,
            totalAmount: parseFloat(document.getElementById(`amount-${id}`).value),
            openingHour: document.getElementById(`open-${id}`).value,
            closingHour: document.getElementById(`close-${id}`).value,
            hasRoof: document.getElementById(`roof-${id}`).checked,
            canShower: document.getElementById(`shower-${id}`).checked
        };

        try {
            const res = await fetch("http://localhost:8080/cancha/updateAny", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(canchaEditada)
            });

            if (res.ok) {
                alert("Cancha actualizada correctamente");
                cargarCanchas();
            } else {
                alert("Error al actualizar cancha");
            }
        } catch (e) {
            console.error(e);
            alert("Error de red");
        }
    }

    async function eliminarCancha(id) {
        if (!confirm("¿Estás seguro de que querés eliminar esta cancha?")) return;

        try {
            const res = await fetch(`http://localhost:8080/cancha/dropCanchaById/${id}`, {
                method: "DELETE"
            });

            if (res.ok) {
                alert("Cancha eliminada");
                cargarCanchas();
            } else {
                alert("Error al eliminar la cancha");
            }
        } catch (e) {
            console.error(e);
            alert("Error de red");
        }
    }

    async function updateCanchaEarnings(canchaId, ownerId, timeframe) {
      try {
        const res = await fetch(`http://localhost:8080/stats/owner/${ownerId}/cancha/${timeframe}`);
        const data = await res.json();

        const canchaStat = data.find(e => e.canchaId === canchaId);
        const total = data
              .filter(e => e.canchaId === canchaId)
              .reduce((sum, e) => sum + (e.totalEarnings || 0), 0);

        const label = document.getElementById(`earnings-${canchaId}`);
        const map = {
          daily: "Hoy",
          weekly: "Semana",
          monthly: "Mes",
          yearly: "Año"
        };
        label.textContent = `$${total.toFixed(2)} (${map[timeframe]})`;
      } catch (err) {
        console.error("Error al obtener estadísticas:", err);
      }
    }
