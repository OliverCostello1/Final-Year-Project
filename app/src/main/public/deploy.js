import { initializeApp } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-app.js";
import { getFirestore, collection, query, where, getDocs, doc, addDoc, updateDoc } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";
import { ethers } from "https://cdn.jsdelivr.net/npm/ethers@5.7.2/dist/ethers.esm.min.js";

// Firebase Configuration
const CONFIG = {
  FIREBASE_CONFIG: {
    apiKey: "AIzaSyAZx5pfgzczeuHWJSZVmes98jgOSmZCcVQ",
    authDomain: "fyp-bidder.firebaseapp.com",
    databaseURL: "https://fyp-bidder-default-rtdb.firebaseio.com",
    projectId: "fyp-bidder",
    storageBucket: "fyp-bidder.firebasestorage.app",
    messagingSenderId: "801916913350",
    appId: "1:801916913350:web:87294a67b770059efa39c7",
    measurementId: "G-BJL419PKWN"
  },
  INFURA_URL: 'https://holesky.infura.io/v3/0a36529195e040c99d49343061af9b0f',
  PRIVATE_KEY: 'bc62f00290873acd14ef2960ce4f30f64cca83910ff7420aa4e2232a6e18e4eb',
  SENDER_ADDRESS: '0x6ad378BAf06450e7845877c0769A4Cf0A8EA9AF0',
  FIRESTORE_COLLECTION: 'bids'
};

// Contract ABI and Bytecode (simplified for illustration)
const contractArtifacts = {
  abi: [
    {
      "inputs": [
        { "internalType": "address", "name": "_bidderWallet", "type": "address" },
        { "internalType": "address", "name": "_auctioneerWallet", "type": "address" },
        { "internalType": "string", "name": "_propertyId", "type": "string" },
        { "internalType": "uint256", "name": "_bidAmount", "type": "uint256" }
      ],
      "stateMutability": "nonpayable",
      "type": "constructor"
    },
    // Other ABI functions...
  ],
  bytecode: "0x608060405234801561001057600080fd5b506040516101c03803806101c083398181016040528051906020015b60405180910390f35b600080fd5b60005481565b600080fd5b6000819050919050565b60006020819052600060a0816000526031600052600160a081015260206000fdfea264697066735822122065a1f987d4b264a1d13d79e2350f5042c070c21d73f2ef70e44bb9114f1b849364736f6c63430006060033"
};

// Initialize Firebase
const app = initializeApp(CONFIG.FIREBASE_CONFIG);
const firestore = getFirestore(app);
console.log('Connected to Firestore');

// Initialize Ethers.js
const provider = new ethers.providers.JsonRpcProvider(CONFIG.INFURA_URL);
const wallet = new ethers.Wallet(CONFIG.PRIVATE_KEY, provider);
console.log('Ethers.js initialized');

// Function to fetch bid data from Firestore
const fetchBidFromFirestore = async (bidId) => {
  const bidRef = collection(firestore, CONFIG.FIRESTORE_COLLECTION);
  const q = query(bidRef, where("bid_id", "==", bidId)); // Query by bid_id
  const querySnapshot = await getDocs(q);
  if (!querySnapshot.empty) {
    const bidData = querySnapshot.docs[0].data(); // Assuming the bid is in the first document
    console.log('Fetched bid data:', bidData);
    return bidData;
  } else {
    throw new Error('No bid found with the given ID');
  }
};

// Function to deploy contract
const deployContract = async (bid) => {
  console.log(`Deploying contract for bid ${bid.bid_id}...`);
  try {
    const bidderWallet = ethers.utils.getAddress(bid.bidder_wallet.trim());
    const auctioneerWallet = ethers.utils.getAddress(bid.auctioneer_wallet.trim());
    const bidAmount = ethers.utils.parseEther(String(bid.bid_amount));

    console.log('Deploying contract with the following values:');
    console.log('Bidder Wallet:', bidderWallet);
    console.log('Auctioneer Wallet:', auctioneerWallet);
    console.log('Bid Amount:', bidAmount.toString());

    const deployTx = new ethers.ContractFactory(contractArtifacts.abi, contractArtifacts.bytecode, wallet);

    // Deploy the contract
    const contract = await deployTx.deploy(bidderWallet, auctioneerWallet, bid.propertyID, bidAmount);
    console.log('Contract deployed. Waiting for confirmation...');

    // Wait for the transaction to be mined
    await contract.deployTransaction.wait();
    console.log('Contract deployed successfully');
    console.log('Transaction Hash:', contract.deployTransaction.hash);
    console.log('Contract Address:', contract.address);

    // Store contract details in Firestore
    await storeContractDetails(bid.bid_id, contract.deployTransaction.hash, contract.address);

    return contract.address;
  } catch (error) {
    console.error('Deployment error:', error);
    throw error;
  }
};

// Function to store contract details in Firestore
const storeContractDetails = async (bidId, transactionHash, contractAddress) => {
  try {
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

deployButton.addEventListener("click", async () => {
  console.log('Deploy button clicked');

  // Example: Specify the bid ID to fetch from Firestore (replace with dynamic value)
  const bidId = '12345'; // Replace with the actual bid ID you want to fetch

  try {
    const bid = await fetchBidFromFirestore(bidId); // Fetch bid from Firestore
    const contractAddress = await deployContract(bid); // Deploy contract
    await updateBidStatus(bid.bid_id);  // Update bid status after deployment
    console.log(`Contract deployed for Bid ID ${bid.bid_id}`);
  } catch (error) {
    console.error(`Error processing bid ${bidId}:`, error);
  }
});
// Function to process multiple bids
export const processBids = async () => {
  try {
    // Fetch all bids from Firestore (or modify the query as needed)
    const bidRef = collection(firestore, CONFIG.FIRESTORE_COLLECTION);
    const querySnapshot = await getDocs(bidRef);

    if (querySnapshot.empty) {
      console.log("No bids found in Firestore.");
      return;
    }

    // Process each bid
    for (const doc of querySnapshot.docs) {
      const bid = doc.data();
      console.log(`Processing Bid ID: ${bid.bid_id}`);

      // Deploy contract for each bid
      try {
        const contractAddress = await deployContract(bid); // Deploy contract
        await updateBidStatus(bid.bid_id);  // Update bid status to contract_generated
        console.log(`Contract deployed for Bid ID ${bid.bid_id}`);
      } catch (error) {
        console.error(`Error processing Bid ID ${bid.bid_id}:`, error);
      }
    }

    console.log("All bids processed.");
  } catch (error) {
    console.error("Error fetching bids from Firestore:", error);
  }
};

// Call processBids to process all bids when needed (e.g., when a button is clicked)
document.getElementById("deployButton").addEventListener("click", processBids);


// Execute main function to process bids
processBids().catch((error) => {
  console.error('Fatal error:', error);
});
