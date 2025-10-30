require('dotenv').config();
const Brevo = require('@getbrevo/brevo');

// Brevo Daten aus .env laden
const apiKey = process.env.BREVO_API_KEY;
const senderEmail = process.env.BREVO_SENDER_EMAIL;
const senderName = process.env.BREVO_SENDER_NAME;

// Brevo-Client initialisieren
const client = new Brevo.TransactionalEmailsApi();
client.setApiKey(Brevo.TransactionalEmailsApiApiKeys.apiKey, apiKey);

// E-Mail-Daten definieren
const emailData = {
  sender: { name: senderName, email: senderEmail },
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