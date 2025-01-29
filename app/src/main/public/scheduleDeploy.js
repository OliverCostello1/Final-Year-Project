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

// Updated Contract ABI and Bytecode
const contractArtifacts = {
  // ABI and Bytecode details here...
};

// Initialize Firebase
const app = initializeApp(CONFIG.FIREBASE_CONFIG);
const db = getFirestore(app);

// Function to process bids
async function processBids() {
  const bidsCollection = collection(db, CONFIG.FIRESTORE_COLLECTION);
  const q = query(bidsCollection, where("status", "==", "pending")); // Filter for pending bids

  try {
    const querySnapshot = await getDocs(q);
    querySnapshot.forEach(async (docSnapshot) => {
      const bidData = docSnapshot.data();
      const bidderWallet = bidData.bidder_wallet;
      const auctioneerWallet = bidData.auctioneer_wallet;
      const propertyId = bidData.property_id;
      const bidAmount = bidData.bid_amount;

      // Set up Ethereum contract interaction
      const provider = new ethers.JsonRpcProvider(CONFIG.INFURA_URL);
      const signer = new ethers.Wallet(CONFIG.PRIVATE_KEY, provider);
      const contract = new ethers.Contract(CONFIG.SENDER_ADDRESS, contractArtifacts.abi, signer);

      // Submit the bid
      try {
        const tx = await contract.submitBid(bidderWallet, auctioneerWallet, propertyId, bidAmount);
        console.log(`Bid for Property ID ${propertyId} submitted with Tx Hash: ${tx.hash}`);

        // Update Firestore with the transaction status (if needed)
        await updateDoc(doc(db, CONFIG.FIRESTORE_COLLECTION, docSnapshot.id), {
          status: 'processed',
          transactionHash: tx.hash
        });
        console.log("Bid processed and Firestore updated!");
      } catch (error) {
        console.error("Error processing bid:", error);
      }
    });
  } catch (error) {
    console.error("Error fetching bids:", error);
  }
}

// Function to schedule bid processing based on user's input (time)
function scheduleDeployScript(time) {
  const scheduledTime = new Date(time);
  const currentTime = new Date();
  const delay = scheduledTime - currentTime;

  if (delay > 0) {
    setTimeout(() => {
      processBids();
      console.log("Scheduled Deploy Script executed at", scheduledTime);
    }, delay);
  } else {
    console.error("Scheduled time is in the past!");
  }
}

// Sample of how to schedule the deploy (this can be tied to the user's UI input)
scheduleDeployScript("2025-01-30T14:00:00"); // Replace with the actual scheduled time
