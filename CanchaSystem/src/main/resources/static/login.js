const params = new URLSearchParams(window.location.search);
    if (params.has("error")) {
        document.write("Usuario o contraseña incorrectos");
    }