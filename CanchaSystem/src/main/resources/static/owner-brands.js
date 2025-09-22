  document.addEventListener("DOMContentLoaded", async () => {
  document.getElementById("addBrandBtn").addEventListener("click", () => {
  document.getElementById("brandForm").style.display = "block";
  });

  document.getElementById("newBrandForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const brandName = document.getElementById("brandNameInput").value;
        await fetch("/canchaBrand/insert", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ brandName })
        });
        location.reload();
      });
      
  try {
    const ownerRes = await fetch("/owner/me");
    const ownerId = await ownerRes.json();

    const brandsRes = await fetch("/canchaBrand/findAllOwnerBrands");
    const brands = await brandsRes.json();
    console.log("brands:", brands);

    if (!brands || brands.length == 0) {
      document.getElementById("brand-list").innerText = "No hay marcas para mostrar.";
      return;
    }

    const earningsRes = await fetch(`/stats/owner/${ownerId}/brand/lifetime`);
    const earnings = await earningsRes.json();

    const list = document.getElementById("brand-list");

    for (const brand of brands) {
      const brandEarning = earnings.find(e => e.brandId === brand.id);
      const total = (brandEarning?.totalEarnings || 0).toFixed(2);

      const li = document.createElement("div");
      li.classList.add("cancha-item");

      li.innerHTML = `
        <div class="cancha-header">
          <span class="brand-name">${brand.brandName}</span>
          <div class="earnings-bar">
            <span class="earnings-label" id="earnings-${brand.id}">$${total} (Total)</span>
            <div class="earnings-buttons">
              <button onclick="updateEarnings(${ownerId}, ${brand.id}, 'daily')">Diario</button>
              <button onclick="updateEarnings(${ownerId}, ${brand.id}, 'weekly')">Semana</button>
              <button onclick="updateEarnings(${ownerId}, ${brand.id}, 'monthly')">Mes</button>
              <button onclick="updateEarnings(${ownerId}, ${brand.id}, 'yearly')">Año</button>
            </div>
          </div>
        </div>
        <div class="cancha-actions">
          <button onclick="location.href='manage-brand.html?brandId=${brand.id}&brandName=${encodeURIComponent(brand.brandName)}'">
            Gestionar Marca
          </button>
        </div>
      `;
      list.appendChild(li);
    }

  } catch (e) {
    console.error("Error cargando marcas:", e);
    document.getElementById("brand-list").innerText = "No se pudieron cargar las marcas.";
  }
});

async function updateEarnings(ownerId, brandId, timeframe) {
  const res = await fetch(`/stats/owner/${ownerId}/brand/${timeframe}`);
  const data = await res.json();
  const brandEntries = data.filter(item => item.brandId === brandId);
  const total = brandEntries.reduce((sum, item) => sum + (item.totalEarnings || 0), 0);
  const label = document.getElementById(`earnings-${brandId}`);
  const labels = {
    daily: "Hoy",
    weekly: "Semana",
    monthly: "Mes",
    yearly: "Año"
  };
  label.textContent = `$${total.toFixed(2)} (${labels[timeframe]})`;
}
