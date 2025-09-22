document.addEventListener("DOMContentLoaded", async () => {
  const requestBtn = document.getElementById("requestOwnerBtn");

  try {
    const nameResponse = await fetch("http://localhost:8080/client/name", { credentials: "include" });
    const nameData = await nameResponse.json();
    document.getElementById("salute").textContent = `¡Bienvenido ${nameData.name || "Cliente"}!`;

    const idResponse = await fetch("http://localhost:8080/client/me", { credentials: "include" });
    const idData = await idResponse.json();
    if (!idData.id) throw new Error("No se pudo obtener el ID del cliente.");
    window.clientId = idData.id;

    try {
      const reqCheck = await fetch(`http://localhost:8080/client/request/${window.clientId}`, {
        credentials: "include"
      });

      if (reqCheck.ok && requestBtn) {
        requestBtn.disabled = true;
        requestBtn.textContent = "Solicitud enviada";
        requestBtn.classList.add("disabled-btn");
      }
    } catch (error) {
      console.log("No hay solicitud previa (o error):", error);
    }

    const balanceResponse = await fetch(`http://localhost:8080/client/${window.clientId}`);
    const balanceData = await balanceResponse.json();
    const balanceElement = document.getElementById("currentBalance");

    if (typeof balanceData.bankClient === "number") {
      balanceElement.textContent = `Saldo actual: $${balanceData.bankClient.toFixed(2)}`;
    } else {
      balanceElement.textContent = "Saldo actual: No disponible";
      console.warn("El campo 'bankClient' no está presente en la respuesta.");
    }

  } catch (error) {
    console.error("Error al cargar datos iniciales:", error);
  }

  if (requestBtn) {
    requestBtn.addEventListener("click", async () => {
      try {
        const requestPayload = { client: { id: window.clientId } };
        const requestResponse = await fetch("http://localhost:8080/client/request", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(requestPayload),
        });

        if (!requestResponse.ok) {
          const error = await requestResponse.json();
          console.error("Error en la solicitud:", error);
          alert(`❌ Error al enviar solicitud: ${error.message || "Error desconocido."}`);
          return;
        }

        alert("✅ Solicitud enviada con éxito.");
        requestBtn.disabled = true;
        requestBtn.textContent = "Solicitud enviada";
        requestBtn.classList.add("disabled-btn");

      } catch (error) {
        console.error("Error de red al enviar la solicitud:", error);
        alert("❌ Error de red al enviar la solicitud. Inténtalo de nuevo.");
      }
    });
  }

  const addMoneyForm = document.getElementById("addMoneyForm");

  addMoneyForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const amount = parseFloat(document.getElementById("amount").value);

    try {
      const response = await fetch(
        `http://localhost:8080/client/addMoneyToClient/${window.clientId}/${amount}`,
        {
          method: "PUT",
          credentials: "include",
        }
      );

      const message = await response.text();
      const responseMessage = document.getElementById("responseMessage");
      responseMessage.textContent = message;
      responseMessage.style.color = "green";

      const newBalanceRes = await fetch(`http://localhost:8080/client/${window.clientId}`, {
        credentials: "include",
      });
      const newBalanceData = await newBalanceRes.json();
      if (typeof newBalanceData.bankClient === "number") {
        document.getElementById("currentBalance").textContent = `Saldo actual: $${newBalanceData.bankClient.toFixed(2)}`;
      }

      document.getElementById("amount").value = "";

    } catch (error) {
      console.error("Error al agregar saldo:", error);
      const responseMessage = document.getElementById("responseMessage");
      responseMessage.textContent = "Error al agregar saldo.";
      responseMessage.style.color = "red";
    }
  });
});
