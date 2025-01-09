// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract BidContract {
    address public bidderWallet;
    address public auctioneerWallet;
    uint public propertyId;
    uint public bidAmount;
    uint public timestamp;

    // Constructor to initialize the contract with the bid details
    constructor(address _bidderWallet, address _auctioneerWallet, uint _propertyId, uint _bidAmount) {
        bidderWallet = _bidderWallet;
        auctioneerWallet = _auctioneerWallet;
        propertyId = _propertyId;
        bidAmount = _bidAmount;
        timestamp = block.timestamp;
    }

    // Getters for contract data
    function getBidDetails() public view returns (address, address, uint, uint, uint) {
        return (bidderWallet, auctioneerWallet, propertyId, bidAmount, timestamp);
    }
}
