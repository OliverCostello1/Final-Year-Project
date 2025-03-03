const functions = require('firebase-functions');
const admin = require('firebase-admin');
const { onSchedule } = require('firebase-functions/v2/scheduler');
const { onCall } = require("firebase-functions/v2/https");
const cors = require('cors');
const { SecretManagerServiceClient } = require('@google-cloud/secret-manager');
// Instantiates a client
const client = new SecretManagerServiceClient();
const projectId = "fyp-bidder";

// Initialize Firebase Admin SDK
admin.initializeApp();
const db = admin.firestore(); // Correct Firestore access
const corsHandler =  cors( { origin:true });
const { ethers, JsonRpcProvider, Wallet, ContractFactory, BigNumber, formatEther  } = require('ethers');

// Import Firestore modules
const {
  getFirestore,
  collection,
  query,
  where,
  getDocs,
  doc,
  addDoc,
  updateDoc
} = require('firebase/firestore');

// ... rest of your code ...
// Firebase Configuration
const CONFIG = {

  INFURA_URL: 'https://holesky.infura.io/v3/0a36529195e040c99d49343061af9b0f',
  PRIVATE_KEY: 'bc62f00290873acd14ef2960ce4f30f64cca83910ff7420aa4e2232a6e18e4eb',
  SENDER_ADDRESS: '0x37Ce0C4A5c1ab7401Ff8e3F12bD60E2aEF0ad20e',
  FIRESTORE_COLLECTION: 'bids'
};

// Updated Contract ABI and Bytecode

// Updated Contract ABI and Bytecode
const contractArtifacts = {
  "abi": [
    {
      "inputs": [
        {
          "internalType": "address",
          "name": "_bidderWallet",
          "type": "address"
        },
        {
          "internalType": "address",
          "name": "_auctioneerWallet",
          "type": "address"
        },
        {
          "internalType": "string",
          "name": "_propertyId",
          "type": "string"
        },
        {
          "internalType": "uint256",
          "name": "_bidAmount",
          "type": "uint256"
        }
      ],
      "stateMutability": "nonpayable",
      "type": "constructor"
    },
    {
      "anonymous": false,
      "inputs": [
        {
          "indexed": true,
          "internalType": "address",
          "name": "bidder",
          "type": "address"
        },
        {
          "indexed": true,
          "internalType": "address",
          "name": "auctioneer",
          "type": "address"
        },
        {
          "indexed": false,
          "internalType": "string",
          "name": "propertyId",
          "type": "string"
        },
        {
          "indexed": false,
          "internalType": "uint256",
          "name": "amount",
          "type": "uint256"
        },
        {
          "indexed": false,
          "internalType": "uint256",
          "name": "timestamp",
          "type": "uint256"
        }
      ],
      "name": "BidCreated",
      "type": "event"
    },
    {
      "inputs": [],
      "name": "auctioneerWallet",
      "outputs": [
        {
          "internalType": "address",
          "name": "",
          "type": "address"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    },
    {
      "inputs": [],
      "name": "bidAmount",
      "outputs": [
        {
          "internalType": "uint256",
          "name": "",
          "type": "uint256"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    },
    {
      "inputs": [],
      "name": "bidderWallet",
      "outputs": [
        {
          "internalType": "address",
          "name": "",
          "type": "address"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    },
    {
      "inputs": [],
      "name": "getBidDetails",
      "outputs": [
        {
          "internalType": "address",
          "name": "",
          "type": "address"
        },
        {
          "internalType": "address",
          "name": "",
          "type": "address"
        },
        {
          "internalType": "string",
          "name": "",
          "type": "string"
        },
        {
          "internalType": "uint256",
          "name": "",
          "type": "uint256"
        },
        {
          "internalType": "uint256",
          "name": "",
          "type": "uint256"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    },
    {
      "inputs": [],
      "name": "propertyId",
      "outputs": [
        {
          "internalType": "string",
          "name": "",
          "type": "string"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    },
    {
      "inputs": [],
      "name": "timestamp",
      "outputs": [
        {
          "internalType": "uint256",
          "name": "",
          "type": "uint256"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    }
  ],
  "bytecode": "0x608060405234801561001057600080fd5b50604051610fa1380380610fa1833981810160405281019061003291906104b4565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff16036100a1576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161009890610594565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610110576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161010790610600565b60405180910390fd5b60008111610153576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161014a90610692565b60405180910390fd5b836000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555082600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600290816101e391906108c9565b508060038190555042600481905550600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1660008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f8b7b31b78ab119d3ca6b16116af7e833b72f01a242c2ad9ca6ed2b5ffb35ffdb600260035460045460405161029a93929190610a2e565b60405180910390a350505050610a6c565b6000604051905090565b600080fd5b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006102ea826102bf565b9050919050565b6102fa816102df565b811461030557600080fd5b50565b600081519050610317816102f1565b92915050565b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b61037082610327565b810181811067ffffffffffffffff8211171561038f5761038e610338565b5b80604052505050565b60006103a26102ab565b90506103ae8282610367565b919050565b600067ffffffffffffffff8211156103ce576103cd610338565b5b6103d782610327565b9050602081019050919050565b60005b838110156104025780820151818401526020810190506103e7565b60008484015250505050565b600061042161041c846103b3565b610398565b90508281526020810184848401111561043d5761043c610322565b5b6104488482856103e4565b509392505050565b600082601f8301126104655761046461031d565b5b815161047584826020860161040e565b91505092915050565b6000819050919050565b6104918161047e565b811461049c57600080fd5b50565b6000815190506104ae81610488565b92915050565b600080600080608085870312156104ce576104cd6102b5565b5b60006104dc87828801610308565b94505060206104ed87828801610308565b935050604085015167ffffffffffffffff81111561050e5761050d6102ba565b5b61051a87828801610450565b925050606061052b8782880161049f565b91505092959194509250565b600082825260208201905092915050565b7f496e76616c696420626964646572206164647265737300000000000000000000600082015250565b600061057e601683610537565b915061058982610548565b602082019050919050565b600060208201905081810360008301526105ad81610571565b9050919050565b7f496e76616c69642061756374696f6e6565722061646472657373000000000000600082015250565b60006105ea601a83610537565b91506105f5826105b4565b602082019050919050565b60006020820190508181036000830152610619816105dd565b9050919050565b7f42696420616d6f756e74206d7573742062652067726561746572207468616e2060008201527f3000000000000000000000000000000000000000000000000000000000000000602082015250565b600061067c602183610537565b915061068782610620565b604082019050919050565b600060208201905081810360008301526106ab8161066f565b9050919050565b600081519050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000600282049050600182168061070457607f821691505b602082108103610717576107166106bd565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b60006008830261077f7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610742565b6107898683610742565b95508019841693508086168417925050509392505050565b6000819050919050565b60006107c66107c16107bc8461047e565b6107a1565b61047e565b9050919050565b6000819050919050565b6107e0836107ab565b6107f46107ec826107cd565b84845461074f565b825550505050565b600090565b6108096107fc565b6108148184846107d7565b505050565b5b818110156108385761082d600082610801565b60018101905061081a565b5050565b601f82111561087d5761084e8161071d565b61085784610732565b81016020851015610866578190505b61087a61087285610732565b830182610819565b50505b505050565b600082821c905092915050565b60006108a060001984600802610882565b1980831691505092915050565b60006108b9838361088f565b9150826002028217905092915050565b6108d2826106b2565b67ffffffffffffffff8111156108eb576108ea610338565b5b6108f582546106ec565b61090082828561083c565b600060209050601f8311600181146109335760008415610921578287015190505b61092b85826108ad565b865550610993565b601f1984166109418661071d565b60005b8281101561096957848901518255600182019150602085019450602081019050610944565b868310156109865784890151610982601f89168261088f565b8355505b6001600288020188555050505b505050505050565b600081546109a8816106ec565b6109b28186610537565b945060018216600081146109cd57600181146109e357610a16565b60ff198316865281151560200286019350610a16565b6109ec8561071d565b60005b83811015610a0e578154818901526001820191506020810190506109ef565b808801955050505b50505092915050565b610a288161047e565b82525050565b60006060820190508181036000830152610a48818661099b565b9050610a576020830185610a1f565b610a646040830184610a1f565b949350505050565b61052680610a7b6000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c8063861d779914610067578063aec2393b14610089578063b80777ea146100a7578063bbac95c9146100c5578063dbffc12c146100e3578063ee7a3b8e14610101575b600080fd5b61006f61011f565b6040516100809594939291906103de565b60405180910390f35b610091610210565b60405161009e9190610438565b60405180910390f35b6100af610216565b6040516100bc9190610438565b60405180910390f35b6100cd61021c565b6040516100da9190610453565b60405180910390f35b6100eb610240565b6040516100f89190610453565b60405180910390f35b610109610266565b604051610116919061046e565b60405180910390f35b600080606060008060008054906101000a900473ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16600260035460045482805461017f906104bf565b80601f01602080910402602001604051908101604052809291908181526020018280546101ab906104bf565b80156101f85780601f106101cd576101008083540402835291602001916101f8565b820191906000526020600020905b8154815290600101906020018083116101db57829003601f168201915b50505050509250945094509450945094509091929394565b60035481565b60045481565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60028054610273906104bf565b80601f016020809104026020016040519081016040528092919081815260200182805461029f906104bf565b80156102ec5780601f106102c1576101008083540402835291602001916102ec565b820191906000526020600020905b8154815290600101906020018083116102cf57829003601f168201915b505050505081565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061031f826102f4565b9050919050565b61032f81610314565b82525050565b600081519050919050565b600082825260208201905092915050565b60005b8381101561036f578082015181840152602081019050610354565b60008484015250505050565b6000601f19601f8301169050919050565b600061039782610335565b6103a18185610340565b93506103b1818560208601610351565b6103ba8161037b565b840191505092915050565b6000819050919050565b6103d8816103c5565b82525050565b600060a0820190506103f36000830188610326565b6104006020830187610326565b8181036040830152610412818661038c565b905061042160608301856103cf565b61042e60808301846103cf565b9695505050505050565b600060208201905061044d60008301846103cf565b92915050565b60006020820190506104686000830184610326565b92915050565b60006020820190508181036000830152610488818461038c565b905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806104d757607f821691505b6020821081036104ea576104e9610490565b5b5091905056fea26469706673582212206e51cb7168ddc90ebd2aa58dc7a96ee9b76b2d2a2d15d2fb4ced011b2ce2856464736f6c634300081c0033",
};




// Initialize Ethers.js
const provider = new JsonRpcProvider(CONFIG.INFURA_URL);
const wallet = new Wallet(CONFIG.PRIVATE_KEY, provider);
console.log('Ethers.js initialized');


// S
exports.getSecrets = functions.https.onRequest(async (req, res) => {
    // Set CORS headers
    res.set('Access-Control-Allow-Origin', 'https://fyp-bidder.web.app'); // Replace with your web app's origin
    res.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.set('Access-Control-Allow-Headers', 'Content-Type');

    // Handle preflight requests (OPTIONS)
    if (req.method === 'OPTIONS') {
        res.status(204).send('');
        return;
    }

    try {
        // Define the secret names
        const secretNames = ['apiKey', 'authDomain', 'databaseURL', 'projectId', 'storageBucket', 'messagingSenderId', 'appId', 'measurementId', 'INFURA_URL', 'PRIVATE_KEY', 'SENDER_ADDRESS', 'abi']; // Added 'abi'
        const secretPaths = secretNames.map(name => `projects/${projectId}/secrets/${name}/versions/latest`);

        // Access the secrets from Google Cloud Secret Manager
        const secretPromises = secretPaths.map(path => client.accessSecretVersion({ name: path }));
        const secretResponses = await Promise.all(secretPromises);

        const config = {};
        secretResponses.forEach(([version], index) => {
            config[secretNames[index]] = version.payload.data.toString('utf8');
        });

        // Structure the Firebase configuration
        const firebaseConfig = {
            apiKey: config.apiKey,
            authDomain: config.authDomain,
            databaseURL: config.databaseURL,
            projectId: config.projectId,
            storageBucket: config.storageBucket,
            messagingSenderId: config.messagingSenderId,
            appId: config.appId,
            measurementId: config.measurementId
        };

        // Structure the additional configuration (INFURA, PRIVATE_KEY, SENDER_ADDRESS)
        const additionalConfig = {
            INFURA_URL: config.INFURA_URL,
            PRIVATE_KEY: config.PRIVATE_KEY,
            SENDER_ADDRESS: config.SENDER_ADDRESS,
            abi: config.abi // Added abi from secrets
        };

        // Parse the abi string into a JSON object
        let contractArtifacts = {
            abi: null,
            bytecode: null,
        };
        try {
            // Remove the outer quotes from the abi string
            const cleanedAbiString = config.abi.replace(/^"|"$/g, '');

            const parsedAbi = JSON.parse(cleanedAbiString);
            contractArtifacts.abi = parsedAbi.abi;
            contractArtifacts.bytecode = parsedAbi.bytecode;
        } catch (parseError) {
            functions.logger.error('Error parsing abi secret:', parseError);
            contractArtifacts = null; // Set to\r\\ null on parse error.
        }

        res.json({
            status: "success",
            FIREBASE_CONFIG: firebaseConfig,
            CONFIG: additionalConfig,
            CONTRACT_ARTIFACTS: contractArtifacts, // Send the parsed artifacts
        });
    } catch (error) {
        functions.logger.error('Error accessing secrets:', error);
        res.status(500).json({ status: 'error', message: error.message });
    }
});
async function getContractCode(address) {
    try {
        // Use the provider's getCode method to fetch the bytecode at the address
        const code = await provider.getCode(address);
        // If the code is empty, the address is likely not a contract
        if (code === '0x') {
            console.log('Address is not a contract.');
        console.log("Contract code:", code)

        } else {
            console.log('Contract code:', code);
        }
    } catch (error) {
        console.error('Error fetching contract code:', error);
    }
}
// Then you can test the connection with:
async function testConnection() {
    try {
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

// Run the test
testConnection();

const fetchBidFromFirestore = async (bidId) => {
  try {
    const bidRef = db.collection(CONFIG.FIRESTORE_COLLECTION);
    const querySnapshot = await bidRef
      .where("bid_id", "==", bidId)
      .where("contract_generated", "==", false) // Query by bid_id and contract_generated flag
      .get();

    if (!querySnapshot.empty) {
      const bidData = querySnapshot.docs[0].data(); // Assuming the bid is in the first document
      console.log('Fetched bid data:', bidData);
      return bidData;
    } else {
      throw new Error('No bid found with the given ID');
    }
  } catch (error) {
    console.error('Error fetching bid data:', error);
    throw error;
  }
};


// Function to store contract details in Firestore
const storeContractDetails = async (bidId, transactionHash, contractAddress) => {
  try {
    const code = await provider.getCode(contractAddress); // Assuming 'provider' is already initialized
    console.log(code);

    const contractRef = db.collection('contracts'); // Use firestore() to access the 'contracts' collection
    await contractRef.add({
      bid_id: bidId,
      transaction_hash: transactionHash,
      contract_address: contractAddress,
      created_at: db.FieldValue.serverTimestamp(), // Using server timestamp
    });

    console.log(`Contract details stored for Bid ID ${bidId}`);
  } catch (error) {
    console.error('Error storing contract details:', error);
  }
};

const updateBidStatus = async (bidId) => {
  try {
    const bidRef = firestore().collection('bids').doc(bidId); // Use firestore() to access the 'bids' collection and the document
    await bidRef.update({ contract_generated: true }); // Use update() method to set the field
    console.log(`Bid ID ${bidId} status updated to contract_generated`);
  } catch (error) {
    console.error('Error updating bid status:', error);
  }
};





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
        const bidAmount = BigNumber.from(bid.bid_amount);
        const status = bid.bid_status.trim();
        console.log('Deploying contract with the following values:');
        console.log('Bidder Wallet:', bidderWallet);
        console.log('Auctioneer Wallet:', auctioneerWallet);
        console.log('Property ID:', propertyID);
        console.log('Property ID:', status);

        console.log('Bid Amount (Hex):', bidAmount.toString());

        const contractFactory = new ContractFactory(contractArtifacts.abi, contractArtifacts.bytecode, wallet);

        // Adjust gas parameters based on network (example using EIP-1559)
        const gasPrice = await provider.getGasPrice(); // Fetching current gas price from network
        const maxPriorityFeePerGas = BigNumber.from("1000000000000"); // 1 Gwei
        const maxFeePerGas = BigNumber.from("2000000000000"); // 1.5 Gwei

        const gasLimit = BigNumber.from(6000000); // Estimate or set a fixed gas limit
        const deploy = await contractFactory.deploy(
            bidderWallet, auctioneerWallet, propertyID, bidAmount, status,
            {
                gasLimit: gasLimit,
                maxPriorityFeePerGas: maxPriorityFeePerGas,
                maxFeePerGas: maxFeePerGas
            }
        );
        const contract = await deploy.wait();
        console.log("Contract:", contract); // Log the entire contract object to ensure it's not undefined


        console.log('Contract deployed. Waiting for confirmation...');
        await contract.deployTransaction.wait();
        console.log('Contract deployed successfully');
        console.log('Transaction Hash:', contract.deployTransaction.hash);
        console.log('Contract Address:', contract.address);

        // Store contract details in Firestore
        await storeContractDetails(bid.bid_id, contract.deployTransaction.hash, contract.address);
        console.log(`Contract details stored for Bid ID ${bid.bid_id}`);

        // Update bid status after deployment
        await updateBidStatus(bid.bid_id);
        console.log(`Bid ID ${bid.bid_id} status updated`);

    } catch (error) {
        console.error(`Error deploying contract for Bid ID ${bid.bid_id}:`, error.message);
    }
};


async function processBids() {
  try {
    console.log("Starting contract deployment...");

    // Initialize provider and signer
    const provider = new JsonRpcProvider(CONFIG.INFURA_URL);
    const wallet = new Wallet(CONFIG.PRIVATE_KEY, provider);
    console.log(`Wallet Address: ${wallet.address}`);

    // Log the wallet balance
    const balance = await provider.getBalance(wallet.address);
    const balanceInEther = formatEther(balance);
    console.log(`Wallet Balance: ${balanceInEther} ETH`);
    const network = await provider.getNetwork();
    console.log("Connected to network:", network);

    // Fetch all bids to process from Firestore using admin.firestore()
    const bidsCollection = db.collection(CONFIG.FIRESTORE_COLLECTION);
    const querySnapshot = await bidsCollection
      .where("contract_generated", "==", false)
      .get();

    if (querySnapshot.empty) {
      console.log("No bids found to process.");
      return;
    }

    for (const bidDoc of querySnapshot.docs) {
      const bid = bidDoc.data();

      if (bid.contract_generated) {
        console.log(`Contract already deployed for Bid ID: ${bid.bid_id}. Skipping....`);
        continue;
      }

      // Validate required fields
      if (!bid.bidder_wallet || !bid.auctioneer_wallet || !bid.propertyID || !bid.bid_amount) {
        console.warn(`Bid ${bid.bid_id} is missing required fields. Skipping...`);
        continue;
      }
      console.log(`Processing Bid ID: ${bid.bid_id}`);

      console.log("Bid Details:", bid);

      try {
        // Deploy the contract with a manual gas limit
        const contractFactory = new ContractFactory(contractArtifacts.abi, contractArtifacts.bytecode, wallet);

        const bidderWallet = bid.bidder_wallet.trim();
        const auctioneerWallet = bid.auctioneer_wallet.trim();
        const bidAmount = bid.bid_amount; // Converts wei to ether
        const propertyID = bid.propertyID.trim();

        console.log("Deploying contract with the following parameters:");
        console.log("Bidder Wallet:", bidderWallet);
        console.log("Auctioneer Wallet:", auctioneerWallet);
        console.log("Property ID:", propertyID);
        console.log("Bid Amount:", bidAmount.toString());

        const gasLimit = 1000000; // Set a manual gas limit
        const contract = await contractFactory.deploy(bidderWallet, auctioneerWallet, propertyID, bidAmount, {
          gasLimit,
        });

        console.log("Contract deployed. Waiting for confirmation...");

        // Wait for deployment confirmation
        await contract.deployTransaction.wait();
        console.log("Contract successfully deployed!");
        console.log("Transaction Hash:", contract.deployTransaction.hash);
        console.log("Contract Address:", contract.address);

        // Store contract details in Firestore
        await storeContractDetails(bid.bid_id, contract.deployTransaction.hash, contract.address);

        // Update bid status in Firestore
        await updateBidStatus(bid.bid_id);
        console.log(`Bid ${bid.bid_id} processed successfully.`);

        return { transactionHash: contract.deployTransaction.hash, contractAddress: contract.address };
      } catch (deploymentError) {
        console.error(`Error deploying contract for Bid ID ${bid.bid_id}:`, deploymentError.message);
      }
    }
  } catch (error) {
    console.error(`Error processing bids: ${error.message}`);
  }
}

exports.updateSchedule = functions.https.onRequest(async (req, res) => {
    const { frequency, dayOfWeek } = req.body;
    console.log('Received Data:', { frequency, dayOfWeek });
    console.log('Request Headers:', req.headers);
    console.log(typeof frequency && typeof dayOfWeek);
        corsHandler(req, res, async () => {
            const { frequency, dayOfWeek } = req.body;

            if (!frequency || !dayOfWeek) {
                return res.status(400).send({ error: 'Missing frequency or dayOfWeek' });
            }

            // Save schedule settings in Firestore
            await db.collection("scheduler").doc("settings").set({ frequency, dayOfWeek });

            return res.status(200).send({ message: "Schedule updated successfully!" });
        });
});


// ✅ Scheduled Cloud Function (Runs Daily at 9 AM)
exports.scheduledProcessBids = onSchedule("every day 18:00", async (event) => {
    try {
        // Retrieve the latest schedule from Firestore
        const docRef = db.collection("scheduler").doc("settings");
        const doc = await docRef.get();

        if (!doc.exists) {
            console.log("⚠ No schedule found. Skipping bid processing.");
            return;
        }

        const { frequency, dayOfWeek } = doc.data();
        const today = new Date().toLocaleString("en-US", { weekday: "long", timeZone: "UTC" });

        // Check if today matches the scheduled day
        if (shouldRunToday(frequency, dayOfWeek, today)) {
            console.log(`🚀 Running scheduled process for: ${frequency} on ${dayOfWeek}`);
            await processBids();
        } else {
            console.log(`⏳ Skipping today (${today}). Next run: ${frequency} on ${dayOfWeek}`);
        }
    } catch (error) {
        console.error("❌ Error in scheduled function:", error);
    }
});

// ✅ Helper Function: Check if Process Should Run Today
function shouldRunToday(frequency, dayOfWeek, today) {
    if (today !== dayOfWeek) return false; // Only run on the selected day

    if (frequency === "Weekly") return true;
    if (frequency === "Bi-Weekly") {
        // Check Firestore for last execution date (not implemented here)
        return true; // Assume true for now
    }
    if (frequency === "Monthly") {
        const dayOfMonth = new Date().getDate();
        return dayOfMonth <= 7; // Run in the first week
    }
    return false;
}
