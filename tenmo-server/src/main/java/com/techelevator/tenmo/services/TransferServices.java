package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.exception.InvalidTransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

public class TransferServices {

    private static final String REJECTED_STATUS = "Rejected";
    private static final String APPROVED_STATUS = "Approved";
    private static final String PENDING_STATUS = "Pending";

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    //Send transfer between registered users
    @Transactional
    public Transfer processTransfer(Transfer incomingTransfer) throws InvalidTransferException {
        //Get account_id for transfer sender & receiver
        Account toAccount = accountDao.getIndividualAccount(incomingTransfer.getAccountToId());
        Account fromAccount = accountDao.getIndividualAccount(incomingTransfer.getAccountFromId());
        //if the transfer amount is more than the account balance, transfer is rejected
        if (incomingTransfer.getTransferStatus().equalsIgnoreCase(PENDING_STATUS)) {
            // if sender account balance < transfer amount will return -1
            if (fromAccount.getBalance().compareTo(incomingTransfer.getAmountToTransfer()) < 0) {
                incomingTransfer.setTransferStatus(REJECTED_STATUS);
            }
            //if sender account balance >= transfer amount, will return 0, 1
            else {
                //Valid transfers have a default value of "approved"
                incomingTransfer.setTransferStatus(APPROVED_STATUS);
                //Update the account balances for the sender & receiver
                toAccount.setBalance(toAccount.getBalance().add(incomingTransfer.getAmountToTransfer()));
                fromAccount.setBalance(fromAccount.getBalance().subtract(incomingTransfer.getAmountToTransfer()));
                accountDao.updateAccount(toAccount);
                accountDao.updateAccount(fromAccount);
            }
        } else {
            throw new InvalidTransferException();
        }
        return incomingTransfer;
    }

    //
    @Transactional
    public Transfer updateTransfer(Transfer updatedTransfer) throws InvalidTransferException {
        //Get account_id for transfer sender & receiver
        Account toAccount = accountDao.getIndividualAccount(updatedTransfer.getAccountToId());
        Account fromAccount = accountDao.getIndividualAccount(updatedTransfer.getAccountFromId());
        //Transfer default status is "approved"
        if (updatedTransfer.getTransferStatus().equalsIgnoreCase(APPROVED_STATUS)) {
            // if sender account balance < transfer amount will return -1
            if (fromAccount.getBalance().compareTo(updatedTransfer.getAmountToTransfer()) < 0) {
                throw new InvalidTransferException();
            }
            //Update account balances and return transfer information
            toAccount.setBalance(toAccount.getBalance().add(updatedTransfer.getAmountToTransfer()));
            fromAccount.setBalance(fromAccount.getBalance().subtract(updatedTransfer.getAmountToTransfer()));
            accountDao.updateAccount(toAccount);
            accountDao.updateAccount(fromAccount);
            return updatedTransfer;
        }
        //If the transfer is not "rejected" then return transfer information
        else if (!updatedTransfer.getTransferStatus().equalsIgnoreCase(REJECTED_STATUS)) {
            return updatedTransfer;
        } else {
            throw new InvalidTransferException();
        }
    }
}
