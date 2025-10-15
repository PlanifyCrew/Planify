const form = document.getElementById("login");
    
form.addEventListener("submit", function (event) {
    event.preventDefault(); // Prevent form submission

    const email = form.querySelector('input[type="email"]').value;
    const password = form.querySelector('input[type="password"]').value;

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
            localStorage.setItem("dummy_token", data.token);
        } else {
            // Handle login error
            console.error('Login failed');
            const errorData = await response.json();
            console.error("Fehler vom Backend:", errorData); // z. B. {error: "...", code: "401"}
        }
    })
    .catch(error => {
        console.error('Login-Error:', error);
    });
});