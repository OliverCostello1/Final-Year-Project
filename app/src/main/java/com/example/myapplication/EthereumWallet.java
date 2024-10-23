package com.example.myapplication;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.security.Security;

public class EthereumWallet {
    public static Credentials createEthereumWallet(String password, File directory) throws Exception {

        // Ensures BouncyCastle is available
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null ) {
            Security.addProvider(new BouncyCastleProvider());
        }

        ECKeyPair ecKeyPair = Keys.createEcKeyPair();

        String walletFileName = WalletUtils.generateWalletFile(password, ecKeyPair, directory, false);

        File walletFile = new File( directory, walletFileName);

        return WalletUtils.loadCredentials(password, walletFile);
    }

    public static void main(String[] args) {
        try {
            // Encryption Password
            String password = "myfyp";
            File directory = new File("");

            Credentials credentials = createEthereumWallet(password, directory);
            System.out.println("Wallet Address: " + credentials.getAddress());
            System.out.println("Private Key: " + credentials.getEcKeyPair().getPrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
