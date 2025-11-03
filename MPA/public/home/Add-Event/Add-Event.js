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
                // Fallback: hide container
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
        });
    }

    // Verwerfen
    const verwerfenBtn = document.getElementById('verwerfen');
    if (verwerfenBtn) {
        verwerfenBtn.addEventListener('click', () => {
            if (typeof window.closeAddEventPopup === 'function') window.closeAddEventPopup();
        });
    }

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

}

// Simple helpers for status messages (used inside the fragment)
function showAddUserSuccess() {
    const el = document.getElementById('addUser-success');
    if (el) { el.style.display = 'inline-block'; el.classList.add('show'); }
}

function showAddUserFailed() {
    const el = document.getElementById('addUser-failed');
    if (el) { el.style.display = 'inline-block'; el.classList.add('show'); }
}
