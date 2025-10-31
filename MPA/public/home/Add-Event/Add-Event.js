const tnListe = [];


document.getElementById("speichern").addEventListener("click", async () => {
    // Werte aus den Eingabefeldern holen
    const titel = document.getElementById("titel").value;
    const datum = document.getElementById("datum").value;
    const beschreibung = document.getElementById("beschreibung").value;
    const startZeit = document.getElementById("startZeit").value;
    const endeZeit = document.getElementById("endeZeit").value;

    // Beispiel-Token (normalerweise aus Login speichern)
    const token = localStorage.getItem("auth_token");

    // Anfrage-Daten vorbereiten
    const data = {
        token: token,
        event: {
            name: titel,
            date: datum,
            description: beschreibung,
            startTime: startZeit,
            endTime: endeZeit
        },
        tnListe: tnListe
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

    try {
        // Teilnehmer E-Mail-Einladung separat ausführen -> bessere Wartbarkeit
        const respEmail = await fetch("http://localhost:8090/api/sendEmail", {
            method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                const result = await response.json();
                alert("Erfolgreich versendet: " + JSON.stringify(result));
            } else {
                alert("Fehler beim E-Mail-Versand");
            }
    } catch (error) {
        console.error(error);
    }
});


document.getElementById("addUser").addEventListener("click", () => {
    const teilnehmer = document.getElementById("input-addUser").value;
    if (teilnehmer && !tnListe.includes(teilnehmer)) {
        tnListe.push(teilnehmer);
        document.getElementById("input-addUser").value = "";
    }
});