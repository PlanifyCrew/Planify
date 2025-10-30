require('dotenv').config();
const Brevo = require('@getbrevo/brevo');

// Brevo API-Key aus .env laden
const apiKey = process.env.BREVO_API_KEY;

// Brevo-Client initialisieren
const client = new Brevo.TransactionalEmailsApi();
client.setApiKey(Brevo.TransactionalEmailsApiApiKeys.apiKey, apiKey);

// E-Mail-Daten definieren
const emailData = {
  sender: { name: 'Planify', email: 'dummy' },
  to: [{ email: 'dummy' , name: 'Test' }],
  subject: 'Test-E-Mail von Brevo',
  htmlContent: '<h1>Hallo!</h1><p>Dies ist eine Test-E-Mail über Brevo.</p>',
};

// E-Mail senden
client.sendTransacEmail(emailData)
  .then(data => {
    console.log('E-Mail erfolgreich gesendet:', data);
  })
  .catch(error => {
    console.error('Fehler beim Senden:', error);
  });