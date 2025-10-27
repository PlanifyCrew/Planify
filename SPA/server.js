const express = require('express');
const path = require('path');
const app = express();

// Statische Dateien aus dem Build-Ordner
app.use(express.static(path.join(__dirname, 'dist/frontend')));

// Alle anderen Routen auf index.html umleiten
app.get('/', (req, res) =>
  res.sendFile('index.html', { root: 'SPA/browser/' }),
);

// Heroku-Port nutzen
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
