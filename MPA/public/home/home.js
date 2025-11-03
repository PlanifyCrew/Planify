document.addEventListener('DOMContentLoaded', function() {
  const calendarEl = document.getElementById('calendar');
  const popup = document.getElementById('addEventPopup');
  const addEventContainer = document.getElementById('addEventContainer');
  const logoutBtn = document.getElementById('logoutBtn');

  // Kalender
  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: 'dayGridMonth',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,dayGridWeek,dayGridDay'
    },
    dateClick: function(info) {
      openAddEventPopup(info.dateStr);
    },
    events: [
      { title: 'Meeting', date: new Date().toISOString().slice(0, 10) }
    ]
  });

  calendar.render();

  logoutBtn.addEventListener('click', function() {
    localStorage.removeItem('auth_token');
    window.location.href = "../auth.html"; // ggf. anpassen
  });

  // Popup öffnen
function openAddEventPopup(date) {
  fetch('add-event/add-event.html')
    .then(res => res.text())
    .then(html => {
      addEventContainer.innerHTML = html;
      popup.classList.remove('hidden');

      // X-Button Listener setzen
      const closeBtn = addEventContainer.querySelector('#close');
      if (closeBtn) {
        closeBtn.addEventListener('click', () => {
          closePopup();
        });
      }

      initAddEventScript(date); // Initialisierung ausführen
    });
}

// Popup schließen
function closePopup() {
  popup.classList.add('hidden');
  addEventContainer.innerHTML = '';
}

  // Zugriff von innerem Skript erlauben
  window.closeAddEventPopup = closePopup;
});
