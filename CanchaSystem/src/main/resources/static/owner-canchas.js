document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("filtroNombre").addEventListener("input", cargarCanchas);
  document.getElementById("filtroTipo").addEventListener("change", cargarCanchas);
  document.getElementById("filtroTecho").addEventListener("change", cargarCanchas);
  document.getElementById("filtroDucha").addEventListener("change", cargarCanchas);

  cargarCanchas();
});

async function cargarCanchas() {
  const lista = document.getElementById("listaCanchas");
  lista.innerHTML = "<li>Cargando canchas...</li>";

  try {
    const res = await fetch("http://localhost:8080/cancha/findallactive");
    const canchas = await res.json();

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
      lista.innerHTML = "<li>No hay canchas que coincidan con los filtros.</li>";
      return;
    }

    lista.innerHTML = "";
    filtradas.forEach(c => {
      const li = document.createElement("li");

      li.innerHTML = `
        <strong>${c.name}</strong>
        - ${c.address}
        (${c.canchaType})<br>
        Precio: $${c.totalAmount}
        - Abre: ${c.openingHour} / Cierra: ${c.closingHour}<br>
        <button onclick="location.href='details-cancha-owner.html?canchaId=${c.id}'">Detalles</button>
        <hr>`;

      lista.appendChild(li);
    });

  } catch (err) {
    console.error("Error al cargar las canchas:", err);
    lista.innerHTML = "<li>Error al cargar las canchas.</li>";
  }
}
