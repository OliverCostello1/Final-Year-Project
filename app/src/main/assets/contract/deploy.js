const { Web3 } = require('web3');
const mysql = require('mysql');
const fs = require('fs');
const solc = require('solc');
require('dotenv').config();

// Add initial debug log
console.log('Starting script...');



// Configuration constants
const CONFIG = {
    INFURA_URL: process.env.INFURA_URL || 'https://holesky.infura.io/v3/0a36529195e040c99d49343061af9b0f',
    PRIVATE_KEY: process.env.PRIVATE_KEY,
    SENDER_ADDRESS: process.env.SENDER_ADDRESS,
    GAS_PRICE_BUFFER: 1.1,
    MAX_GAS_PRICE: '50000000000',
    RETRY_ATTEMPTS: 3,
    RETRY_DELAY: 5000,
    
};


// Function to compile the Solidity contract
const compileSolidityContract = () => {
    console.log('Compiling contract...');
    const contractSource = fs.readFileSync('BidContract.sol', 'utf8');
    
    const input = {
        language: 'Solidity',
        sources: {
            'BidContract.sol': {
                content: contractSource
            }
        },
        settings: {
            outputSelection: {
                '*': {
                    '*': ['*']
                }
            }
        }
    };

    const compiled = JSON.parse(solc.compile(JSON.stringify(input)));
    const contract = compiled.contracts['BidContract.sol']['BidContract'];
    
    return {
        abi: contract.abi,
        bytecode: contract.evm.bytecode.object
    };
};

// Initialize Web3 with proper error handling
const initializeWeb3 = () => {
    console.log('Initializing Web3...');
    try {
        const web3Instance = new Web3(CONFIG.INFURA_URL); // Correct for v4.x.x
        console.log('Web3 initialized successfully');
        return web3Instance;
    } catch (error) {
        console.error('Failed to initialize Web3:', error);
        throw error;
    }
};

// Create database pool with detailed logging
const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    port: process.env.DB_PORT || 8000, 
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'fyp',
    connectionLimit: 10
});

// Log the connection parameters (for debugging)
console.log('Database connection parameters:', {
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD ? '*****' : 'not set',
    database: process.env.DB_NAME,
    port: process.env.DB_PORT
});

// Test database connection
pool.getConnection((err, connection) => {
    if (err) {
        console.error('Error connecting to DB:', err.code, err.message);
        if (err.code === 'ECONNREFUSED') {
            console.error('Connection refused. Please check MySQL server status and configuration.');
        }
        process.exit(1);
    } else {
        console.log('Successfully connected to the database');
        connection.release();  // Release connection back to the pool
    }
});

const deployContract = async (web3, contract, bid) => {
    console.log(`Deploying contract for bid ${bid.bid_id}...`);

    const deployTx = contract.deploy({
        data: contract.bytecode,
        arguments: [
            bid.bidder_wallet,
            bid.auctioneer_wallet,
            bid.property_id,
            web3.utils.toWei(bid.bid_amount.toString(), 'ether') // Convert to string explicitly
        ]
    });

    const gas = await deployTx.estimateGas();
    const gasPrice = await web3.eth.getGasPrice();
    const balance = await web3.eth.getBalance(CONFIG.SENDER_ADDRESS);
    console.log(`Wallet balance: ${web3.utils.fromWei(balance, 'ether')} ETH`);


    const signedTx = await web3.eth.accounts.signTransaction(
        {
            data: deployTx.encodeABI(),
            gas: Math.floor(Number(gas) * CONFIG.GAS_PRICE_BUFFER * 1.1),
            gasPrice: gasPrice,
            from: CONFIG.SENDER_ADDRESS, // Explicitly specify the sender address

        },
        CONFIG.PRIVATE_KEY
    );

    console.log(`Passing bid amount to toWei: ${bid.bid_amount.toString()}`);
    console.log(`Converted to Wei: ${web3.utils.toWei(bid.bid_amount.toString(), 'ether')}`);

    const receipt = await web3.eth.sendSignedTransaction(signedTx.rawTransaction);
    return receipt;
};



// Update bid status in database
const updateBidStatus = (bidId, contractAddress) => {
    return new Promise((resolve, reject) => {
        const query = 'UPDATE bids SET contract_generated = 1 WHERE bid_id = ?';
        pool.query(query, [contractAddress, bidId], (error) => {
            if (error) {
                reject(error);
            } else {
                resolve();
            }
        });
    });
};

// Main function
const checkNewBidsAndDeploy = async () => {
    console.log('Starting checkNewBidsAndDeploy...');
    
    try {
        // Initialize Web3
        const web3 = initializeWeb3();
        
        // Compile contract
        const contractArtifacts = compileSolidityContract();
        console.log('Contract compiled successfully');

        // Create contract instance
        const Contract = new web3.eth.Contract(contractArtifacts.abi);
        Contract.options.data = contractArtifacts.bytecode;

        // Query database for new bids
        pool.query("SELECT * FROM bids WHERE contract_generated = 0", async (error, results) => {
            if (error) {
                console.error("Database query failed:", error);
                process.exit(1);
            }

            if (!results || results.length === 0) {
                console.log("No new bids found.");
                process.exit(0);
            }

            console.log(`Found ${results.length} bids to process`);

            for (const bid of results) {
                try {
                    const receipt = await deployContract(web3, Contract, bid);
                    await updateBidStatus(bid.bid_id, receipt.contractAddress);
                    
                    console.log(`Contract deployed successfully for Bid ID ${bid.bid_id}:`);
                    console.log(`- Contract Address: ${receipt.contractAddress}`);
                    console.log(`- Transaction Hash: ${receipt.transactionHash}`);
                    console.log(`- Gas Used: ${receipt.gasUsed}`);
                } catch (error) {
                    console.error(`Failed to process bid ${bid.bid_id}:`, error);
                    continue;
                }
            }
            
            process.exit(0);
        });
    } catch (error) {
        console.error('Error in main process:', error);
        process.exit(1);
    }
};

// Execute main function
checkNewBidsAndDeploy().catch(error => {
    console.error('Fatal error:', error);
    process.exit(1);
});

// Handle process termination
process.on('SIGINT', () => {
    console.log('Received SIGINT. Cleaning up...');
    pool.end(() => {
        console.log('Database pool closed.');
        process.exit(0);
    });
});