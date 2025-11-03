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
  // Use absolute path to the Add-Event fragment and ensure its CSS/JS are loaded
  fetch('/home/Add-Event/Add-Event.html')
    .then(res => {
      if (!res.ok) throw new Error('Failed to load add-event fragment: ' + res.status);
      return res.text();
    })
    .then(html => {
      // Parse the fragment and remove any <link> or <script> tags so the browser
      // doesn't try to load relative assets like "Add-Event.css" (which would
      // resolve to "/Add-Event.css" and cause 404s). We'll load the CSS/JS
      // explicitly with absolute paths below.
      try {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        // remove link and script tags from fragment
        doc.querySelectorAll('link, script').forEach(n => n.remove());
        addEventContainer.innerHTML = doc.body.innerHTML;
      } catch (e) {
        // fallback: inject raw HTML
        console.warn('DOMParser failed, injecting raw HTML', e);
        addEventContainer.innerHTML = html;
      }

      popup.classList.remove('hidden');

      // Ensure CSS for the popup is loaded (absolute path)
      const cssHref = '/home/Add-Event/Add-Event.css';
      console.log('Loading add-event CSS:', cssHref);
      if (!document.querySelector(`link[href="${cssHref}"]`)) {
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = cssHref;
        document.head.appendChild(link);
      }

      // Load the script and call its initializer once loaded
      const scriptSrc = '/home/Add-Event/Add-Event.js';
      console.log('Loading add-event script:', scriptSrc);
      // Remove any previous dynamic script
      const prev = document.querySelector(`script[data-dyn-script="${scriptSrc}"]`);
      if (prev) prev.remove();

      const s = document.createElement('script');
      s.src = scriptSrc;
      s.setAttribute('data-dyn-script', scriptSrc);
      s.onload = () => {
        // initAddEventScript is provided by Add-Event.js
        if (typeof initAddEventScript === 'function') {
          initAddEventScript(date);
        } else {
          console.warn('initAddEventScript is not defined after loading', scriptSrc);
        }
      };
      s.onerror = (e) => console.error('Failed to load add-event script', e);
      document.body.appendChild(s);
    })
    .catch(err => {
      console.error(err);
      alert('Fehler beim Laden des Event-Formulars.');
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
