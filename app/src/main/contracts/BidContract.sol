// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract BidContract {
    address public bidderWallet;
    address public auctioneerWallet;
    string public propertyId;
    uint public bidAmount;
    uint public timestamp;

    event BidCreated(
        address indexed bidder,
        address indexed auctioneer,
        string propertyId,
        uint amount,
        uint timestamp
    );

    constructor(
        address _bidderWallet,
        address _auctioneerWallet,
        string memory _propertyId,
        uint _bidAmount
    ) {
        require(_bidderWallet != address(0), "Invalid bidder address");
        require(_auctioneerWallet != address(0), "Invalid auctioneer address");
        require(_bidAmount > 0, "Bid amount must be greater than 0");

        bidderWallet = _bidderWallet;
        auctioneerWallet = _auctioneerWallet;
        propertyId = _propertyId;
        bidAmount = _bidAmount;
        timestamp = block.timestamp;

        emit BidCreated(
            bidderWallet,
            auctioneerWallet,
            propertyId,
            bidAmount,
            timestamp
        );
    }

    // Returns the bid details
    function getBidDetails() public view returns (
        address,
        address,
        string memory,
        uint,
        uint
    ) {
        return (
            bidderWallet,
            auctioneerWallet,
            propertyId,
            bidAmount,
            timestamp
        );
    }
}
