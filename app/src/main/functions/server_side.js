const { SecretManagerServiceClient } = require('@google-cloud/secret-manager');

// Instantiates a client
const client = new SecretManagerServiceClient();

// Secret names
const secretNames = [
  'apiKey',
  'appId',
  'authDomain',
  'databaseURL',
  'INFURA_URL',
  'measurementId',
  'messagingSenderId',
  'projectId',
  'SENDER_ADDRESS',
  'storageBucket'
];

// Replace with your GCP project ID
const projectId = 'fyp-bidder';

exports.getSecrets = async (req, res) => {
  try {
    // Build resource names for the secret versions
    const secretPaths = secretNames.map(name => `projects/${projectId}/secrets/${name}/versions/latest`);

    // Fetch all secrets in parallel
    const secretPromises = secretPaths.map(path => client.accessSecretVersion({ name: path }));
    const secretResponses = await Promise.all(secretPromises);

    // Extract and map secrets
    const secrets = secretResponses.reduce((acc, [version], index) => {
      acc[secretNames[index]] = version.payload.data.toString('utf8');
      return acc;
    }, {});

    // Send secrets securely (Consider masking sensitive data in production)
    res.status(200).json(secrets);
  } catch (error) {
    console.error('Error accessing secrets:', error);
    res.status(500).send('Error accessing secrets');
  }
};
