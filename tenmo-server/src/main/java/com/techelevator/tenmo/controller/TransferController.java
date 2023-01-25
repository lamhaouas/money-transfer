package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.InvalidTransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.services.TransferServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private final TransferDao transferDao;
    private final AccountDao accountDao;
    private final TransferServices transferServices = new TransferServices();

    public TransferController(@Qualifier("ActiveTransferDao") TransferDao transferDao, @Qualifier("ActiveAccountDao") AccountDao accountDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        transferServices.setAccountDao(accountDao);
    }

    //HTTP GET request to view current balance
    //Show balance for current user
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{userId}/user/balance", method = RequestMethod.GET)
    public List<Account> getListAccounts(@PathVariable int userId) {
        return accountDao.getAccountsByUserID(userId);
    }


    //HTTP GET to view all accounts
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<Account> getAllAccounts() {
        return accountDao.getAccounts();
    }

    //HTTP GET to view username of a particular account_id
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{accountId}/account", method = RequestMethod.GET)
    public String getUserNameFromAccountId(@PathVariable int accountId) {
        return accountDao.getUserNameByAccountId(accountId);
    }

    //HTTP POST to create a new transfer
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public Transfer postTransfer(@RequestBody @Valid Transfer transfer) throws InvalidTransferException, SQLException {
        if (transfer.getTransferType().equals("Send")) {
            transfer = transferServices.processTransfer(transfer);
        }
        transferDao.addTransfer(transfer);
        return transfer;
    }

    //HTTP GET to view a transfer details of a transfer (by id)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{id}/transfer", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int id) {
        return transferDao.getTransfer(id);
    }




    //HTTP GET to view transfer history for the current user
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{userId}/user/history", method = RequestMethod.GET)
    public List<Transfer> getAllUserTransfers(@PathVariable int userId) {
        List<Transfer> transfers = new ArrayList<>();
        List<Account> accounts = accountDao.getAccountsByUserID(userId);
        for (Account account : accounts) {
            transfers.addAll(transferDao.getTransferByAccountNumber(account.getAccountId()));
        }
        return transfers;
    }

    //HTTP GET to view transfers of certain status for the current user (used to view pending)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{userId}/user/{requestStatus}/transfer", method = RequestMethod.GET)
    public List<Transfer> getTransfersByStatusCodeAndUser(@PathVariable int userId, @PathVariable String requestStatus) {
        return transferDao.getTransfersByUserIdAndTransferStatus(userId, requestStatus);
    }


    //HTTP PUT to transfer tenmo and update the account and transfer tables in DB
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transfer/pay", method = RequestMethod.PUT)
    public boolean changeRequest(@RequestBody Transfer transfer) throws InvalidTransferException {
        if (transfer == null) {
            throw new InvalidTransferException();
        }
        try {
            transferDao.updateTransfer(transferServices.updateTransfer(transfer));
            return true;
        } catch (InvalidTransferException e) {
            return false;
        }
    }

}
