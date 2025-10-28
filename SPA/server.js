const express = require('express');
const path = require('path');
const app = express();

const staticPath = path.join(__dirname, 'dist/tasksspa/browser');
app.use(express.static(staticPath));

// Express 5.x: param name muss angegeben werden
app.all("/*splat", (req, res) => {
  res.sendFile("index.html", { root: "dist/tasksspa/browser/" });
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
