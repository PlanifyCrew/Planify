// This script is loaded dynamically when the Add-Event fragment is injected.
// Provide an initializer function that home.js can call.
function initAddEventScript(initialDate) {
    const tnListe = [];

    // If date input exists, set it
    const dateInput = document.getElementById('datum');
    if (dateInput && initialDate) {
        dateInput.value = initialDate;
    }

    // Close button: use the global function provided by server page
    const closeBtn = document.getElementById('close');
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            if (typeof window.closeAddEventPopup === 'function') {
                window.closeAddEventPopup();
            } else {
                // Fallback: hide container( Ich fall gleich auf Gewalt back)
                const cont = document.querySelector('.container');
                if (cont) cont.style.display = 'none';
            }
        });
    }

    // Speichern
    const speichernBtn = document.getElementById('speichern');
    if (speichernBtn) {
        speichernBtn.addEventListener('click', async () => {
            const titel = document.getElementById('titel').value;
            const datum = document.getElementById('datum').value;
            const beschreibung = document.getElementById('beschreibung').value;
            const startZeit = document.getElementById('startZeit').value;
            const endeZeit = document.getElementById('endeZeit').value;
            const token = localStorage.getItem('auth_token');

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
        })};

            try {
                const response = await fetch('http://localhost:8090/api/event', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
    try {
        const response = await fetch('http://localhost:8090/api/event', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            alert('Erfolgreich gespeichert: ' + JSON.stringify(result));
        } else {
            alert('Fehler beim Speichern (' + response.status + ')');
        }
    } catch (error) {
        console.error('Netzwerkfehler:', error);
        alert('Server nicht erreichbar!');
    }
             

        try {
            const respEmail = await fetch("http://localhost:8090/api/sendEmail", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (respEmail.ok) {
                const result = await respEmail.json();
                alert("Einladungen versendet: " + JSON.stringify(result));
                showAddUserSuccess();
            } else {
                alert("Fehler beim E-Mail-Versand");
                showAddUserFailed();
            }
        } catch (error) {
            console.error("E-Mail Fehler:", error);
            showAddUserFailed();
        }
    ;

    // Event Listener für Verwerfen
    document.getElementById("verwerfen").addEventListener("click", () => {
        document.querySelector(".container").style.display = "none";
    });

    // Teilnehmer hinzufügen
    const addUserBtn = document.getElementById('addUser-btn');
    if (addUserBtn) {
        addUserBtn.addEventListener('click', () => {
            const teilnehmer = document.getElementById('input-addUser').value;
            if (teilnehmer && !tnListe.includes(teilnehmer)) {
                tnListe.push(teilnehmer);
                document.getElementById('input-addUser').value = '';
            }
        });
    }
    document.getElementById("addUser-btn").addEventListener("click", () => {
        const teilnehmer = document.getElementById("input-addUser").value.trim();
        if (teilnehmer && !tnListe.includes(teilnehmer)) {
            tnListe.push(teilnehmer);
            document.getElementById("input-addUser").value = "";
            addEmailToPopup(teilnehmer);
        }
    });

    // Event Listener für Löschen
    document.getElementById("deleteEvent-btn")?.addEventListener("click", async () => {
        const eventId = document.getElementById("eventId")?.value;
        const token = localStorage.getItem("auth_token");

        if (!eventId) {
            alert("Kein Event ausgewählt.");
            return;
        }

        const data = {
            token: token,
            event_id: eventId
        };

        try {
            const response = await fetch("http://localhost:8090/api/delete-event", {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("Event erfolgreich gelöscht.");
                document.querySelector(".container").style.display = "none";
            } else {
                alert("Fehler beim Löschen (" + response.status + ")");
            }
        } catch (error) {
            console.error("Löschfehler:", error);
            alert("Server nicht erreichbar!");
        }
    });

    // Teilnehmer entfernen (Delegation)
    document.getElementById("userTableBody").addEventListener("click", (e) => {
        if (e.target.classList.contains("removeUser-btn")) {
            const row = e.target.closest("tr");
            const email = row.querySelector("td").textContent;
            tnListe.splice(tnListe.indexOf(email), 1);
            row.remove();
        }
    });

}

// Simple helpers for status messages (used inside the fragment)
// Teilnehmer in Tabelle anzeigen
function addEmailToPopup(email) {
    const tableBody = document.getElementById("userTableBody");
    if (!tableBody) return;

    const row = document.createElement("tr");
    row.innerHTML = `
        <td>${email}</td>
        <td><button type="button" class="removeUser-btn">Entfernen</button></td>
    `;
    tableBody.appendChild(row);
}

// Statusmeldungen
function showAddUserSuccess() {
    const el = document.getElementById('addUser-success');
    if (el) { el.style.display = 'inline-block'; el.classList.add('show'); }
}
    const el = document.getElementById("addUser-success");
    el.style.display = "inline-block";
    el.classList.add("show");
}

function showAddUserFailed() {
    const el = document.getElementById('addUser-failed');
    if (el) { el.style.display = 'inline-block'; el.classList.add('show'); }
}

    const el = document.getElementById("addUser-failed");
    el.style.display = "inline-block";
    el.classList.add("show");
