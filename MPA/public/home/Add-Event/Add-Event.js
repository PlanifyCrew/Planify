document.getElementById("speichern").addEventListener("click", async () => {
    // Werte aus den Eingabefeldern holen
    const titel = document.getElementById("titel").value;
    const datum = document.getElementById("datum").value;
    const beschreibung = document.getElementById("beschreibung").value;
    const startZeit = document.getElementById("startZeit").value;
    const endeZeit = document.getElementById("endeZeit").value;

    // Beispiel-Token (normalerweise aus Login speichern)
    const token = localStorage.getItem("userToken") || "123";

    // Anfrage-Daten vorbereiten
    const data = {
        token: token,
        event: {
            name: titel,
            date: datum,
            description: beschreibung,
            startTime: startZeit,
            endTime: endeZeit,
        }
    };

    try {
        const response = await fetch("http://localhost:8090/api/event", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            alert("Erfolgreich gespeichert: " + JSON.stringify(result));
        } else {
            alert("Fehler beim Speichern (" + response.status + ")");
        }
    } catch (error) {
        console.error("Netzwerkfehler:", error);
        alert("Server nicht erreichbar!");
    }
});