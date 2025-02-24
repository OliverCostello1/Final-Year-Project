onst functions = require('firebase-functions');
const {SecretManagerServiceClient} = require('@google-cloud/secret-manager');

const client = new SecretManagerServiceClient();

exports.getConfig = functions.https.onRequest(async (req, res) => {
  try {
    const [version] = await client.accessSecretVersion({
      name: 'projects/<YOUR_PROJECT_ID>/secrets/<YOUR_SECRET_NAME>/versions/latest', // Replace with your Secret Manager details
    });
    const firebaseConfig = JSON.parse(version.payload.data.toString());

    //Only send what's needed to the client
    const clientConfig = {
        apiKey: firebaseConfig.apiKey,
        authDomain: firebaseConfig.authDomain,
        // ... only send necessary data
    };

    res.json(clientConfig);
  } catch (error) {
    console.error('Error retrieving config:', error);
    res.status(500).send('Error retrieving configuration');
  }
});