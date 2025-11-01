require('dotenv').config();
// sendEmail.js als Express-Server
const express = require('express');
const Brevo = require('@getbrevo/brevo');
const app = express();

app.use(express.json());

// Brevo Daten aus .env laden
const apiKey = process.env.BREVO_API_KEY;
const senderEmail = process.env.BREVO_SENDER_EMAIL;
const senderName = process.env.BREVO_SENDER_NAME;

// Brevo-Client initialisieren
const client = new Brevo.TransactionalEmailsApi();
client.setApiKey(Brevo.TransactionalEmailsApiApiKeys.apiKey, apiKey);

// POST-Endpunkt zum E-Mail-Versand
app.post('/sendEmail', (req, res) => {
  const { email, name, subject, htmlContent } = req.body;

  if (!email || !subject || !htmlContent) {
    return res.status(400).json({ error: 'Fehlende Felder: email, subject oder htmlContent' });
  }

  // E-Mail-Daten definieren
  const emailData = {
    sender: { name: senderName, email: senderEmail },
    to: [{ email: email , name: name }],
    subject: subject,
    htmlContent: htmlContent
  };

  // E-Mail senden
  client.sendTransacEmail(emailData)
    .then(data => {
      console.log('E-Mail erfolgreich gesendet:', data);
    })
    .catch(error => {
      console.error('Fehler beim Senden:', error);
    });
});

  // Server starten
const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
  console.log(`Brevo-E-Mail-Service läuft auf Port ${PORT}`);
});