// SPDX-License-Identifier: MDT
pragma solidity ^0.8.0;

// contract to register user with wallet
contract userRegister {
    struct User {
        string email;
        string first_name;
        string last_name;
        string house_address;
        string role;
        address wallet_address;
    }

    mapping(address => User) public users;

    event userRegistry(

        address indexed wallet_address,
        string email,
        string first_name,
        string last_name,
        string house_address,
        string role 
    );

    // Registers new user 
    function registerUser( 
        string memory email,
        string memory first_name,
        string memory last_name,
        string memory house_address,
        string memory role 
    )
    public {
        require(users[msg.sender].wallet_address == address(0) , "User already exists!");

        users[msg.sender] = User({
            email: email,
            first_name: first_name,
            last_name: last_name,
            house_address: house_address,
            role: role,
            wallet_address: msg.sender 
        });

        emit userRegistry(msg.sender, email, first_name, last_name, house_address, role);

    }
}