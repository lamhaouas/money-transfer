package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.util.List;

public interface AccountDao {

    List<Account> getAccounts();

    List<Account> getAccountsByUserID(int userId);

    Account getIndividualAccount(int accountId);

    String getUserNameByAccountId(int accountId);

    void updateAccount(Account accountToUpdate);
}

