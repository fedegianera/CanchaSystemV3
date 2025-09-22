let ownerId;
let roleId;

window.addEventListener("DOMContentLoaded", cargarDatosDueño);

async function cargarDatosDueño() {
    try {
        const res = await fetch("http://localhost:8080/owner/me");
        ownerId = await res.json();

        const ownerres = await fetch(`http://localhost:8080/owner/${ownerId}`);
        const owner = await ownerres.json();
        roleId = owner.role.id;

        document.getElementById("name").value = owner.name;
        document.getElementById("lastName").value = owner.lastName;
        document.getElementById("username").value = owner.username;
        document.getElementById("mail").value = owner.mail;
        document.getElementById("cellNumber").value = owner.cellNumber || '';
    } catch (error) {
        alert("Error al cargar los datos del cliente");
        console.error(error);
    }
}

function habilitarEdicion() {
    const campos = ["name", "lastName", "username", "mail", "cellNumber"];
    campos.forEach(id => {
        const input = document.getElementById(id);
        input.readOnly = false;
        input.classList.add("editable");
    });
    document.querySelector("button[onclick='guardarCambios()']").classList.remove("hidden");
}

async function guardarCambios() {
    const usernameIngresado = document.getElementById("username").value;

    const usernameOriginal = (await (await fetch(`http://localhost:8080/owner/${ownerId}`)).json()).username;

    if (usernameIngresado !== usernameOriginal) {
        const usernameDisponible = await verificarUsernameDisponible(usernameIngresado);
        if (usernameDisponible) {
            alert("El nombre de usuario ya está en uso. Por favor, elegí otro.");
            return;
        }
    }

    const ownerActualizado = {
        id: ownerId,
        name: document.getElementById("name").value,
        lastName: document.getElementById("lastName").value,
        username: usernameIngresado,
        mail: document.getElementById("mail").value,
        cellNumber: document.getElementById("cellNumber").value
    };

    if (!validarEmail(ownerActualizado.mail)) {
        alert("El email no tiene un formato válido.");
        return;
    }

    const emailValid = await validarEmailConAPI(ownerActualizado.mail);
    if (!emailValid) {
        alert("El email ingresado no es válido");
        return;
    }

    try {
        const res = await fetch("http://localhost:8080/owner/update", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(ownerActualizado)
        });

        if (res.ok) {
            alert("Datos actualizados correctamente 🎉");
            window.location.href = "/login.html";
        } else {
            alert("Error al guardar los cambios");
        }
    } catch (e) {
        console.error("Error en la actualización:", e);
        alert("No se pudo guardar");
    }
}


function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

async function validarEmailConAPI(email) {
    const apiKey = 'be22398a0ca646a08843a2d4ff1f8970';
    const verifyUrl = `https://emailvalidation.abstractapi.com/v1/?api_key=${apiKey}&email=${encodeURIComponent(email)}`;

    try {
        const response = await fetch(verifyUrl);
        const data = await response.json();

        return data.deliverability === "DELIVERABLE" && data.is_valid_format.value === true;
    } catch (error) {
        console.error("Error al validar el correo con AbstractAPI:", error);
        return false;
    }
}

async function verificarUsernameDisponible(username) {
    try {
        const res = await fetch(`http://localhost:8080/owner/verifyUsername?username=${encodeURIComponent(username)}`);
        return await res.json();
    } catch (error) {
        console.error("Error al verificar username:", error);
        return false;
    }
}