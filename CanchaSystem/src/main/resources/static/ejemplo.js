async function cargar(tipo, endpoint) {
    const lista = document.getElementById('resultado');
    lista.innerHTML = `<li>Cargando ${tipo}...</li>`;

    try {
      const res = await fetch('http://localhost:8080' + endpoint);
      const contentType = res.headers.get('content-type');

      let data = null;

      if (contentType && contentType.includes('application/json')) {
        data = await res.json();
      } else {
        const text = await res.text();
        console.error("Respuesta inesperada del servidor:", text);
        throw new Error("Respuesta inesperada del servidor (no es JSON)");
      }

      if (!res.ok) {
        throw new Error(data.error || `Error al obtener ${tipo}`);
      }

      lista.innerHTML = '';

      if (!Array.isArray(data) || data.length === 0) {
        lista.innerHTML = `<li>No hay ${tipo} para mostrar.</li>`;
        return;
      }

      data.forEach(obj => {
        const li = document.createElement('li');
        li.textContent = formatear(obj);
        lista.appendChild(li);
      });

    } catch (err) {
      lista.innerHTML = `<li style="color: red;">${err.message}</li>`;
    }
  }

  function formatear(obj) {
    return Object.entries(obj).map(([k, v]) => {
      if (typeof v === 'object' && v !== null) {
        return `${k}: { ${formatear(v)} }`;
      }
      return `${k}: ${v}`;
    }).join(' | ');
  }

  async function buscar(tipo, endpoint) {
  const lista = document.getElementById('resultado');
  lista.innerHTML = `<li>Buscando ${tipo}...</li>`;

  try {
    const res = await fetch('http://localhost:8080' + endpoint, {
      credentials: 'include'
    });
    const contentType = res.headers.get('content-type');

    let data = null;

    if (contentType && contentType.includes('application/json')) {
      data = await res.json();
    } else {
      const text = await res.text();
      console.error("Respuesta inesperada del servidor:", text);
      throw new Error("Respuesta inesperada del servidor (no es JSON)");
    }

    if (!res.ok) {
      throw new Error(data.error || `Error al buscar ${tipo}`);
    }

    lista.innerHTML = '';

    if (!Array.isArray(data) || data.length === 0) {
      lista.innerHTML = `<li>No hay ${tipo} para mostrar.</li>`;
      return;
    }

    data.forEach(obj => {
      const li = document.createElement('li');
      li.textContent = formatear(obj);
      lista.appendChild(li);
    });

  } catch (err) {
    lista.innerHTML = `<li style="color: red;">${err.message}</li>`;
  }
}