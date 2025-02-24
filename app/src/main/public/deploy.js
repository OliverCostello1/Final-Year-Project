import { getFirestore, collection, query, where, getDocs, doc, addDoc, updateDoc } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";
import {fetchAndActivate, getValue, getRemoteConfig } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-remote-config.js";
import { getFunctions, httpsCallable } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-functions.js';
import { ethers } from "https://cdn.jsdelivr.net/npm/ethers@5.7.2/dist/ethers.esm.min.js";
import { initializeApp, getApps, deleteApp } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-app.js";
// Firebase Configuration
const CONFIG = {

  FIRESTORE_COLLECTION: 'bids'
};



// Declare a global variable to hold the config
let secretsConfig = {}; // Declare secretsConfig globally

async function fetchSecrets() {
  try {
    const response = await fetch('https://getsecrets-obhajuq66q-uc.a.run.app'); // Replace with your actual function URL
    const data = await response.json();

    if (data.status === "success") {
      secretsConfig = data.CONFIG;  // Save the config for later use

      // Log the secrets for debugging
      console.log(`INFURA_URL: ${secretsConfig.INFURA_URL}`);
      console.log(`SENDER_ADDRESS: ${secretsConfig.SENDER_ADDRESS}`);
      console.log(`PRIVATE_KEY: ${secretsConfig.PRIVATE_KEY}`);

      return secretsConfig; // Return the secretsConfig for use in the rest of the code
    } else {
      console.error('Error fetching secrets:', data.message);
      return null; // Return null if fetch fails
    }
  } catch (error) {
    console.error('Error fetching secrets:', error);
    return null; // Return null if there is an error in fetching
  }
}
let provider = null; // Declare provider as null globally
let wallet = null;   // Declare wallet as null globally

// Initialize provider and wallet after fetching secrets
export async function initializeProvider() {
  try {
    const secrets = await fetchSecrets(); // Fetch secrets from your endpoint

    if (!secrets || !secrets.INFURA_URL || !secrets.PRIVATE_KEY) {
      throw new Error("Missing required secrets. Cannot initialize provider.");
    }

    // Initialize the provider using the INFURA URL
    provider = new ethers.providers.JsonRpcProvider(secrets.INFURA_URL);
    console.log("✅ Provider initialized with URL:", secrets.INFURA_URL);

    // Initialize the wallet using the PRIVATE_KEY and provider
    wallet = new ethers.Wallet(secrets.PRIVATE_KEY, provider);
    console.log("✅ Wallet initialized with address:", await wallet.getAddress());

    // Optional: Log network info for debugging
    const network = await provider.getNetwork();
    console.log("🌐 Connected to network:", network.name, "Chain ID:", network.chainId);

    // Optional: Check wallet balance
    const balance = await provider.getBalance(wallet.address);
    console.log(`💰 Wallet Balance: ${ethers.utils.formatEther(balance)} ETH`);

  } catch (error) {
    console.error("❌ Error initializing provider and wallet:", error.message);
  }
}



// Export the secretsConfig and provider for use in other parts of the app
export { secretsConfig, provider };
async function setupAndTestConnection() {
    await initializeProvider(); // Wait for provider to be initialized
    const connectionStatus = await testConnection(); // Then test the connection
    if (connectionStatus) {
        console.log("Connection is successful!");
    } else {
        console.log("Connection failed.");
    }
}

// Then you can test the connection with:
async function testConnection() {
    try {
        // Ensure provider is initialized before accessing it
        if (!provider) {
            throw new Error("Provider is not initialized.");
        }

        // Try to get the latest block number
        const blockNumber = await provider.getBlockNumber();
        console.log("Connected! Current block number:", blockNumber);

        // Get the network info
        const network = await provider.getNetwork();
        console.log("Connected to network:", network.name);

        return true;
    } catch (error) {
        console.error("Connection failed:", error.message);
        return false;
    }
}

export let firestore;

export async function fetchAndInitFirebase() {
  try {
    if (getApps().length > 0) {
      console.log('Firebase already initialized.');
      firestore = getFirestore();  // Initialize firestore when Firebase is already initialized
      return firestore;
    }

    // Initialize with a temporary app
    const tempApp = initializeApp({ apiKey: "temp", authDomain: "temp", projectId: "temp" });

    // Fetch Firebase config from the server
    const cloudFunctionURL = 'https://getsecrets-obhajuq66q-uc.a.run.app';
    const response = await fetch(cloudFunctionURL);

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const { status, FIREBASE_CONFIG } = await response.json();

    if (status !== "success" || !FIREBASE_CONFIG) {
      throw new Error('Invalid Firebase configuration received.');
    }

    // Clean up the temporary app and initialize Firebase with the fetched config
    await deleteApp(tempApp);

    const app = initializeApp(FIREBASE_CONFIG);
    console.log('Firebase initialized with config:', FIREBASE_CONFIG);

    firestore = getFirestore(app);  // Set the firestore variable after initialization
    return firestore;

  } catch (error) {
    console.error('Error during Firebase initialization:', error);
  }
}
let CONTRACT_ARTIFACTS = null;
async function getContractArtifacts() {
    const cloudFunctionURL = 'https://getsecrets-obhajuq66q-uc.a.run.app';
    try {
        const response = await fetch(cloudFunctionURL);
        if (!response.ok) {
            throw new Error(`Failed to fetch secrets. Status: ${response.status} ${response.statusText}`);
        }
        const data = await response.json();
        const artifacts = data.CONTRACT_ARTIFACTS;
        if (!artifacts) {
            console.error("Contract artifacts not found.", artifacts);
            return null;
        }
        if (!artifacts.abi || !artifacts.bytecode) {
            console.error("Contract artifacts missing abi or bytecode.", artifacts);
            return null;
        }
        CONTRACT_ARTIFACTS = artifacts;
        console.log("Contract artifacts:", artifacts);
        return artifacts;

    } catch (error) {
        console.error("Error fetching artifacts:", error);
        return null;
    }
}export {CONTRACT_ARTIFACTS};


getContractArtifacts();


// Function to fetch bid data from Firestore
const fetchBidFromFirestore = async (bidId) => {
  const bidRef = collection(firestore, CONFIG.FIRESTORE_COLLECTION);
  const q = query(bidRef, where("bid_id", "==", bidId), where("contract_generated", "==",  false)); // Query by bid_id
  const querySnapshot = await getDocs(q);
  if (!querySnapshot.empty) {
    const bidData = querySnapshot.docs[0].data(); // Assuming the bid is in the first document
    console.log('Fetched bid data:', bidData);
    return bidData;
  } else {
    throw new Error('No bid found with the given ID');
  }
};


// Function to store contract details in Firestore
const storeContractDetails = async (bidId, transactionHash, contractAddress) => {
  try {
  console.log("Storing contract details", bidId, transactionHash, contractAddress)
  const code = await provider.getCode(contractAddress);
  console.log(code);

    const contractRef = collection(firestore, 'contracts');
    await addDoc(contractRef, {
      bid_id: bidId,
      transaction_hash: transactionHash,
      contract_address: contractAddress,
      created_at: new Date(),
    });
    console.log(`Contract details stored for Bid ID ${bidId}`);
  } catch (error) {
    console.error('Error storing contract details:', error);
    console.error('Full error:', error);

  }
};

// Function to update bid status in Firestore
const updateBidStatus = async (bidId) => {
  try {
    const bidRef = doc(firestore, 'bids', bidId);
    await updateDoc(bidRef, { contract_generated: true });
    console.log(`Bid ID ${bidId} status updated to contract_generated`);
  } catch (error) {
    console.error('Error updating bid status:', error);
  }
};

// Event listener for deploy button
const deployButton = document.getElementById("deployButton");



const deployContract = async (bid) => {
    console.log(`Deploying contract for bid ${bid.bid_id}...`);
    try {
        // Validate required bid fields
        if (!bid.bidder_wallet || !bid.auctioneer_wallet || !bid.propertyID || !bid.bid_amount) {
            throw new Error('Missing required bid properties.');
        }

        const bidderWallet = bid.bidder_wallet.trim();
        const auctioneerWallet = bid.auctioneer_wallet.trim();
        const propertyID = bid.propertyID.trim();
        const bidAmount = bid.bid_amount;
        const bidStatus = bid.bid_status;

        console.log('Deploying contract with the following values:');
        console.log('Bidder Wallet:', bidderWallet);
        console.log('Auctioneer Wallet:', auctioneerWallet);
        console.log('Property ID:', propertyID);
        console.log('Bid Amount (Hex):', bidAmount.toString());
        console.log('Bid Status: ', bidStatus)
        if (!CONTRACT_ARTIFACTS || !CONTRACT_ARTIFACTS.abi) {
          throw new Error("CONTRACT_ARTIFACTS is not loaded correctly.");
        } else {
            console.log("Contract artifacts", CONTRACT_ARTIFACTS)
        }

        const contractFactory = new ethers.ContractFactory(CONTRACT_ARTIFACTS.abi, CONTRACT_ARTIFACTS.bytecode, wallet);

        // Adjust gas parameters based on network (example using EIP-1559)
        const gasPrice = await provider.getGasPrice(); // Fetching current gas price from network
        const maxPriorityFeePerGas = ethers.BigNumber.from("1000000000000"); // 1 Gwei
        const maxFeePerGas = ethers.BigNumber.from("2000000000000"); // 1.5 Gwei

        const gasLimit = ethers.BigNumber.from(6000000); // Estimate or set a fixed gas limit

        const contract = await contractFactory.deploy(
            bidderWallet, auctioneerWallet, propertyID, bidAmount,
            {
                gasLimit: gasLimit,
                maxPriorityFeePerGas: maxPriorityFeePerGas,
                maxFeePerGas: maxFeePerGas
            }
        );

        console.log('Contract deployed. Waiting for confirmation...');
        displayLog('Contract deployed. Waiting for confirmation...');
        await contract.deployTransaction.wait();
        console.log('Contract deployed successfully');
        displayLog('Contract deployed successfully');
        console.log('Transaction Hash:', contract.deployTransaction.hash);
        displayLog('Transaction Hash:', contract.deployTransaction.hash);
        console.log('Contract Address:', contract.address);
        displayLog('Contract Address:', contract.address);

        if (contract.deployTransaction && contract.deployTransaction.hash && contract.address) {
            // Store contract details in Firestore
            storeContractDetails(bid.bid_id, contract.deployTransaction.hash, contract.address);

            // Update bid status after deployment
            await updateBidStatus(bid.bid_id);
            console.log(`Bid ID ${bid.bid_id} status updated`);
        } else {
            console.error(`Contract details not available for Bid ID ${bid.bid_id}.`);
        }


    } catch (error) {
        console.error(`Error deploying contract for Bid ID ${bid.bid_id}:`, error.message);
    }


};




export async function processBids() {
  try {
    if (!wallet || !provider) {
      console.error("❌ Wallet or provider is not initialized. Call initializeProvider() first.");
      return;
    }

    console.log("🚀 Starting contract deployment...");

    // Fetch bids from Firestore
    const bidsCollection = collection(firestore, CONFIG.FIRESTORE_COLLECTION);
    const q = query(bidsCollection, where("contract_generated", "==", false));
    const querySnapshot = await getDocs(q);

    if (querySnapshot.empty) {
      console.log("📭 No bids found to process.");
      displayLog("No bids found to process");
      return;
    }

    for (const bidDoc of querySnapshot.docs) {
      const bid = bidDoc.data();

      if (bid.contract_generated) {
        console.log(`⚠️ Contract already deployed for Bid ID: ${bid.bid_id}. Skipping...`);
        continue;
      }

      // Validate essential fields
      if (!bid.bidder_wallet || !bid.auctioneer_wallet || !bid.propertyID || !bid.bid_amount) {
        console.warn(`❗ Bid ${bid.bid_id} is missing required fields. Skipping...`);
        continue;
      }

      console.log(`🛠️ Processing Bid ID: ${bid.bid_id}`, bid);

      try {
        // Deploy the contract using the global wallet
        const contractFactory = new ethers.ContractFactory(CONTRACT_ARTIFACTS.abi, CONTRACT_ARTIFACTS.bytecode, wallet);

        const gasLimit = 1000000; // Manual gas limit
        const contract = await contractFactory.deploy(
          bid.bidder_wallet.trim(),
          bid.auctioneer_wallet.trim(),
          bid.propertyID.trim(),
          bid.bid_amount,
          { gasLimit }
        );

        console.log("⏳ Contract deployed. Waiting for confirmation...");

        // Ensure deployment transaction is valid before proceeding
        if (!contract || !contract.deployTransaction) {
          console.error(`❌ Deployment failed for Bid ID: ${bid.bid_id}`);
          continue; // Skip to the next bid if deployment fails
        }

        // Wait for deployment confirmation
        await contract.deployTransaction.wait();
        console.log("Contract successfully deployed!");
        console.log("Transaction Hash:", contract.deployTransaction.hash);
        console.log("Contract Address:", contract.address);

        // Store contract details and update bid status
        console.log(bid.bid_id, contract.deployTransaction.hash, contract.address)
        await storeContractDetails(bid.bid_id, contract.deployTransaction.hash, contract.address);
        await updateBidStatus(bid.bid_id);

        console.log(`✅ Bid ${bid.bid_id} processed successfully.`);

      } catch (deploymentError) {
        console.error(`❌ Error deploying contract for Bid ID ${bid.bid_id}:`, deploymentError);
      }
    }
  } catch (error) {
    console.error(`❌ Error processing bids: ${error.message}`);
  }

  return {transaction_hash, contractAddress};
}




