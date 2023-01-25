package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component("ActiveAccountDao")
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public JdbcAccountDao(DataSource dataSource) throws SQLException {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
        dataSource.getConnection().setAutoCommit(false);
    }

    //View a list of all registered users in the account table
    @Override
    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance " + "FROM account " + "ORDER BY account_id ASC;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            accounts.add(mapRowToAccount(sqlRowSet));
        }
        return accounts;
    }

    //View all columns from the account table for a specific user_id
    @Override
    public List<Account> getAccountsByUserID(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance " + "FROM account " + "WHERE user_id = ? " + "ORDER BY account_id ASC;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId);
        while (sqlRowSet.next()) {
            accounts.add(mapRowToAccount(sqlRowSet));
        }
        return accounts;
    }

    //View all columns from the account table for a specific account_id
    @Override
    public Account getIndividualAccount(int accountId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance " + "FROM account " + "WHERE account_id = ?;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        if (sqlRowSet.next()) {
            account = mapRowToAccount(sqlRowSet);
        }
        return account;
    }


    //Return the tenmo-user.username for a specific account_id
    @Override
    public String getUserNameByAccountId(int accountId) {
        String sql = "SELECT tu.username " + "FROM account AS a " + "JOIN tenmo_user AS tu ON tu.user_id = a.user_id " + "WHERE a.account_id = ?;";
        return jdbcTemplate.queryForObject(sql, String.class, accountId);
    }

    //Update account balances (used during transfer)
    @Override
    public void updateAccount(Account accountToUpdate) {
        String sql = "UPDATE account " + "SET user_id = ?, balance = ? " + "WHERE account_id = ?;";
        jdbcTemplate.update(sql, accountToUpdate.getUserId(), accountToUpdate.getBalance(), accountToUpdate.getAccountId());
    }


    private Account mapRowToAccount(SqlRowSet sqlRowSet) {
        Account account = new Account();
        account.setAccountId(sqlRowSet.getInt("account_id"));
        account.setUserId(sqlRowSet.getInt("user_id"));
        account.setBalance(sqlRowSet.getBigDecimal("balance"));
        return account;
    }

}
