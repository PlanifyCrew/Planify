const express = require('express');
const path = require('path');
const app = express();

const PORT = process.env.PORT || 3000;

// Log all requests before processing
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} ${req.method} ${req.url}`);
  next();
});

// Serve static files from public directory with proper MIME types
app.use(express.static(path.join(__dirname, 'public'), {
  setHeaders: (res, filePath, stat) => {
    if (filePath.endsWith('.js')) {
      res.set('Content-Type', 'application/javascript');
    } else if (filePath.endsWith('.css')) {
      res.set('Content-Type', 'text/css');
    } else if (filePath.endsWith('.png')) {
      res.set('Content-Type', 'image/png');
    }
  },
  fallthrough: true // Try next middleware if file not found
}));

// Handle root route
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'home', 'home.html'));
});

// Handle auth route
app.get('/auth', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'auth', 'auth.html'));
});

// Handle home route
app.get('/home', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'home', 'home.html'));
});

  // Serve auth page under /auth/ so relative links resolve to /auth/...
  app.get('/auth.html', (req, res) => {
    // redirect to the auth folder so relative assets (auth.css, auth.js, images)
    // are requested under /auth/* which matches the express.static mount.
    res.redirect(301, '/auth/auth.html');
  });

// Handle 404s
app.use((req, res, next) => {
  console.error(`404: ${req.method} ${req.url} not found`);
  res.status(404).send(`File not found: ${req.url}`);
});

// Error handling
app.use((err, req, res, next) => {
  console.error(`Error: ${err.stack}`);
  res.status(500).send('Internal Server Error');
});

// Start server
app.listen(PORT, () => {
  console.log(`${new Date().toISOString()} Server started on port ${PORT}`);
  console.log(`Static files served from: ${path.join(__dirname, 'public')}`);
  console.log('Routes:');
  console.log('  / -> public/home/home.html');
  console.log('  /home/* -> public/home/*');
  console.log('  /* -> public/*');
});
