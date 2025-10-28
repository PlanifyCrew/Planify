const express = require('express');
const path = require('path');
const app = express();

// Absoluter Pfad zum SPA Build
const staticPath = path.join(__dirname, 'dist', 'frontend');

// Statische Dateien bereitstellen
app.use(express.static(staticPath));

// SPA-Fallback (Express v5)
app.all('/*splat', (req, res) => {
  res.sendFile('index.html', { root: staticPath });
});

// Port starten
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
