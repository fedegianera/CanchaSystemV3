document.addEventListener("DOMContentLoaded", async () => {
  const params = new URLSearchParams(window.location.search);
  const brandId = params.get("brandId");
  const brandName = params.get("brandName");

  document.getElementById("brand-name").textContent = brandName;
  const canchaContainer = document.getElementById("cancha-list-container");

  const ownerRes = await fetch("/owner/me");
  const ownerId = await ownerRes.json();

  async function cargarCanchas() {
    canchaContainer.innerHTML = "<p>Cargando canchas...</p>";
    const res = await fetch(`/cancha/getCanchasByBrandId/${brandId}`);
    const canchas = await res.json();

    if (canchas.length == 0) {
      canchaContainer.innerHTML = "<p>Esta marca aún no tiene canchas.</p>";
      return;
    }

    const earningsRes = await fetch(`/stats/owner/${ownerId}/cancha/lifetime`);
    const allEarnings = await earningsRes.json();

    canchaContainer.innerHTML = "";

    canchas.forEach((cancha) => {
      const canchaEarning = allEarnings.find((e) => e.canchaId === cancha.id);
      const earningsLabel = `${(canchaEarning?.totalEarnings || 0).toFixed(2)} (Total)`;


      const div = document.createElement("div");
      div.classList.add("cancha-item");

      div.innerHTML = `
        <div class="cancha-header">
          <div>
            <strong>${cancha.name}</strong><br>
            <span>${cancha.address}</span>
          </div>
          <div class="earnings-bar">
            <span class="earnings-label" id="earnings-${cancha.id}">
              $${(canchaEarning?.totalEarnings || 0).toFixed(2)} (Total)
            </span>
            <div class="earnings-buttons">
              <button onclick="updateCanchaEarnings(${ownerId}, ${cancha.id}, 'daily')">Diario</button>
              <button onclick="updateCanchaEarnings(${ownerId}, ${cancha.id}, 'weekly')">Semana</button>
              <button onclick="updateCanchaEarnings(${ownerId}, ${cancha.id}, 'monthly')">Mes</button>
              <button onclick="updateCanchaEarnings(${ownerId}, ${cancha.id}, 'yearly')">Año</button>
            </div>
          </div>

        </div>
        <div class="cancha-actions">
          <button onclick="window.location.href='/details-my-cancha-owner.html?canchaId=${cancha.id}'">Ver</button>
          <button onclick="window.location.href='edit-cancha.html?canchaId=${cancha.id}'">Modificar</button>
          <button onclick="eliminarCancha(${cancha.id})" class="btn-eliminar">Eliminar</button>
        </div>
      `;

      canchaContainer.appendChild(div);
    });
  }

  window.updateCanchaEarnings = async (ownerId, canchaId, timeframe) => {
    const res = await fetch(`/stats/owner/${ownerId}/cancha/${timeframe}`);
    const data = await res.json();
    const canchaEntries = data.filter((entry) => entry.canchaId === canchaId);
    const total = canchaEntries.reduce((sum, e) => sum + (e.totalEarnings || 0), 0);
    const label = document.getElementById(`earnings-${canchaId}`);
    const labels = {
      daily: "Hoy",
      weekly: "Semana",
      monthly: "Mes",
      yearly: "Año"
    };
    label.textContent = `$${total.toFixed(2)} (${labels[timeframe]})`;
  };

  document.getElementById("btn-eliminar-marca").addEventListener("click", async () => {
    if (confirm("¿Seguro que querés eliminar esta marca? Esta acción eliminará también todas sus canchas.")) {
      const res = await fetch(`/canchaBrand/deleteCanchaBrand/${brandId}`, { method: "DELETE" });
      if (res.ok) {
        alert("Marca eliminada correctamente");
        window.location.href = "owner-brands.html";
      } else {
        alert("No se pudo eliminar la marca");
      }
    }
  });

  document.getElementById("btn-show-form").addEventListener("click", () => {
    document.getElementById("cancha-form").style.display = "block";
  });

  document.getElementById("btn-cancel").addEventListener("click", () => {
    document.getElementById("cancha-form").style.display = "none";
    document.getElementById("newCanchaForm").reset();
  });

  document.getElementById("btn-volver").addEventListener("click", () => {
    window.location.href = "owner-brands.html";
  });

  document.getElementById("newCanchaForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    const canchaData = {
      name: form.name.value,
      address: form.address.value,
      totalAmount: parseFloat(form.totalAmount.value),
      openingHour: form.openingHour.value,
      closingHour: form.closingHour.value,
      hasRoof: form.hasRoof.checked,
      canShower: form.canShower.checked,
      canchaType: form.canchaType.value,
      working: form.working.checked,
      active: true,
      brand: { id: brandId }
    };

    const res = await fetch("/cancha/insert", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(canchaData)
    });

    if (res.ok) {
      alert("Cancha agregada correctamente");
      form.reset();
      form.style.display = "none";
      cargarCanchas();
    } else {
      alert("Error al agregar cancha");
    }
  });

  window.eliminarCancha = async (id) => {
    if (confirm("¿Seguro que quieres eliminar esta cancha?")) {
      const res = await fetch(`/cancha/dropMyCanchaById/${id}`, { method: "DELETE" });
      if (res.ok) {
        alert("Cancha eliminada");
        cargarCanchas();
      } else {
        alert("Error al eliminar cancha");
      }
    }
  };

  cargarCanchas();
});
