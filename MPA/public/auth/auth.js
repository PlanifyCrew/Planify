const formLogin = document.getElementById("login");
const formSignUp = document.getElementById("signup");

formLogin.addEventListener("submit", function (event) {
    event.preventDefault(); // Prevent form submission

    const email = formLogin.querySelector('input[type="email"]').value;
    const password = formLogin.querySelector('input[type="password"]').value;

    fetch('http://localhost:8090/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    })
    .then( async response => {
        if (response.ok) {
            // Login successful
            const data = await response.json();
            console.log('Login successful');
            console.log("Antwort vom Backend:", data); // z. B. {token: "...", code: "200"}
            // Token lokal speichern
            localStorage.setItem("auth_token", data.token);

            // Weiterleitung zur Homepage, wenn Login-Daten korrekt sind
            if (data.token !== "OFF")
                window.location.href = "../home/home.html";
            else
                showErrorMessage();
        } else {
            // Handle login error
            console.error('Login failed');
            const errorData = await response.json();
            console.error("Fehler vom Backend:", errorData); // z. B. {error: "...", code: "401"}

            showErrorMessage();
        }
    })
    .catch(error => {
        console.error('Login-Error:', error);
        showErrorMessage();
    });
});


formSignUp.addEventListener("submit", function (event) {
    event.preventDefault(); // Prevent form submission

    const name = formSignUp.querySelector('input[type="text"]').value;
    const email = formSignUp.querySelector('input[type="email"]').value;
    const password = formSignUp.querySelector('input[type="password"]').value;

    fetch('http://localhost:8090/api/user', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
    })
    .then(async response => {
        if (response.ok) {
            // Registration successful
            const data = await response.json();
            console.log('Registration successful');
            console.log("Antwort vom Backend:", data); // z. B. {token: "...", code: "200"}

            // Weiterleitung zur Homepage, da automatischer Login
            // window.location.href = "../home/home.html";
        } else {
            // Handle registration error
            console.error('Registration failed');
            const errorData = await response.json();
            console.error("Fehler vom Backend:", errorData); // z. B. {error: "...", code: "401"}
        }
    })
    .catch(error => {
        console.error('Registration-Error:', error);
    });
});


function showErrorMessage() {
    let errorMessage = document.getElementById("error-message");
    errorMessage.classList.add("show");
}