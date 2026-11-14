document.getElementById("login").addEventListener("click", function () {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    fetch("/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username, password: password })
    })
        .then(resp => resp.json())
        .then(resp => {
            if (resp.code === 200) {
                location.href = "/index.html";
            } else {
                alert(resp.message);
            }
        });
});
