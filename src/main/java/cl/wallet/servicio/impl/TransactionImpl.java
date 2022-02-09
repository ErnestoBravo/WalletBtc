package cl.wallet.servicio.impl;

import java.io.File;
import java.math.BigInteger;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

//import com.google.bitcoin.core.Address;
//import com.google.bitcoin.core.BlockChain;
//import com.google.bitcoin.core.NetworkParameters;
//import com.google.bitcoin.core.PeerGroup;
//import com.google.bitcoin.core.Transaction;
//import com.google.bitcoin.core.Wallet;
//import com.google.bitcoin.core.Wallet.SendRequest;
//import com.google.bitcoin.params.TestNet3Params;
//import com.google.bitcoin.script.Script;
//import com.google.bitcoin.store.BlockStore;
//import com.google.bitcoin.store.MemoryBlockStore;

@Service
public class TransactionImpl {

	public String sendBtc() {
		// We use the WalletAppKit that handles all the boilerplate for us. Have a look at the Kit.java example for more details.
        NetworkParameters params = TestNet3Params.get();
        WalletAppKit kit = new WalletAppKit(params, new File("."), "sendrequest-example") {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeyChainGroupSize() < 1) {
//					new ECKey();
					wallet().importKey(ECKey.fromPrivate(new BigInteger("70830002697343512228364346785782890265336994493369545840331792061353250356756")));
				}
            }
        };
        
        
        kit.startAsync();
        kit.awaitRunning();


        // How much coins do we want to send?
        // The Coin class represents a monetary Bitcoin value.
        // We use the parseCoin function to simply get a Coin instance from a simple String.
        Coin value = Coin.parseCoin("0.0002");

        // To which address you want to send the coins?
        // The Address class represents a Bitcoin address.
        LegacyAddress to = LegacyAddress.fromBase58(params, "2N6eiPHWdCL6Eh4DGe9a35i5UyadUcGc2w8");
        System.out.println("Send money to: " + to.toString());

        // There are different ways to create and publish a SendRequest. This is probably the easiest one.
        // Have a look at the code of the SendRequest class to see what's happening and what other options you have: https://bitcoinj.github.io/javadoc/0.11/com/google/bitcoin/core/Wallet.SendRequest.html
        // 
        // Please note that this might raise a InsufficientMoneyException if your wallet has not enough coins to spend.
        // When using the testnet you can use a faucet to get testnet coins.
        // In this example we catch the InsufficientMoneyException and register a BalanceFuture callback that runs once the wallet has enough balance.
        try {
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);
            System.out.println("coins sent. transaction hash: " + result.tx.getTxId());
            // you can use a block explorer like https://www.biteasy.com/ to inspect the transaction with the printed transaction hash. 
        } catch (InsufficientMoneyException e) {
            System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
            System.out.println("Send money to: " + kit.wallet().currentReceiveAddress().toString());

            // Bitcoinj allows you to define a BalanceFuture to execute a callback once your wallet has a certain balance.
            // Here we wait until the we have enough balance and display a notice.
            // Bitcoinj is using the ListenableFutures of the Guava library. Have a look here for more information: https://github.com/google/guava/wiki/ListenableFutureExplained
            ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(value, BalanceType.AVAILABLE);
            FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                @Override
                public void onSuccess(Coin balance) {
                    System.out.println("coins arrived and the wallet now has enough balance");
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("something went wrong");
                }
            };
            Futures.addCallback(balanceFuture, callback, MoreExecutors.directExecutor());
        }

        // shutting down 
        kit.stopAsync();
        kit.awaitTerminated();
    
		return ":o)";
	}

	public String consultaSaldo() {
	
	        
        return "Hola Saldo";
	}
	
	
	

}
