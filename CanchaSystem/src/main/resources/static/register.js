    document.getElementById("registerForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const email = document.getElementById("mail").value;

    const apiKey = 'be22398a0ca646a08843a2d4ff1f8970';
    const verifyUrl = "https:" + "//emailvalidation.abstractapi.com/v1/?api_key=" + apiKey + "&email=" + encodeURIComponent(email);

    try {
        const verifyResponse = await fetch(verifyUrl);
        const verifyData = await verifyResponse.json();

        if (verifyData.deliverability !== "DELIVERABLE") {
            document.getElementById("message").textContent = "Correo inválido o no entregable.";
            return;
        }

        const client = {
            name: document.getElementById("name").value,
            lastName: document.getElementById("lastname").value,
            username: document.getElementById("username").value,
            password: document.getElementById("password").value,
            mail: email,
            cellNumber: document.getElementById("cellNumber").value,
        };

        const response = await fetch("/client/insert", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(client)
        });

        if (response.ok) {
            const messageElem = document.getElementById("message");
            messageElem.textContent = "¡Registro exitoso! Redirigiendo...";
            messageElem.classList.add("exito");
            setTimeout(() => {
                window.location.href = "/login.html";
            }, 2000);
        } else {
            const data = await response.json();
            console.log(data);
            document.getElementById("message").textContent = data.error;
        }

    } catch (error) {
        document.getElementById("message").textContent = "Error verificando el correo o en el servidor.";
    }
});
