package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TenmoService {

    private final String basicAPIUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public TenmoService(String basicAPIUrl){
        this.basicAPIUrl = basicAPIUrl;
    }

    //  HTTP GET request to view current balance
    public Account[] viewCurrentBalance(AuthenticatedUser user){
        Account[] currentBalance = null;
        try {
            ResponseEntity<Account[]> response = restTemplate.exchange(basicAPIUrl
                            + user.getUser().getId() + "/user/balance", HttpMethod.GET, makeAuthEntity(user.getToken()),
                    Account[].class);
            currentBalance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return currentBalance;
    }
    // HTTP GET request to get all accounts
    public Account[] getAllAccounts(AuthenticatedUser user){
        Account[] accounts = null;
        try {
            ResponseEntity<Account[]> response = restTemplate.exchange(basicAPIUrl + "/accounts", HttpMethod.GET, makeAuthEntity(user.getToken()), Account[].class);
            accounts = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return accounts;
    }
    //converts account ID to username to use between different tables in the database
    public String convertAccountIdToUserName(AuthenticatedUser user, int accountId){
        String userName = null;
        try{
            ResponseEntity<String> response = restTemplate.exchange(basicAPIUrl + "/" + accountId + "/account", HttpMethod.GET, makeAuthEntity(user.getToken()), String.class);
            userName = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return userName;
    }

    //shows a list of all transfers done by user
    public Transfer[] viewTransferHistory(AuthenticatedUser user){
        Transfer[] transferHistory = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(basicAPIUrl + user.getUser().getId() + "/user/history", HttpMethod.GET, makeAuthEntity(user.getToken()), Transfer[].class);
            transferHistory = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transferHistory;
    }
    //Allows viewing of the pending requests that the user may have
    public Transfer[] viewPendingRequests(AuthenticatedUser user){
        Transfer[] pendingRequests = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(basicAPIUrl + user.getUser().getId() + "/user/Pending/transfer", HttpMethod.GET, makeAuthEntity(user.getToken()), Transfer[].class);
            pendingRequests = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return pendingRequests;
    }
    //Transfer function!!
    public Transfer makeTransfer(AuthenticatedUser user, BigDecimal amountToSend, String transferType, int accountToSendFrom, int accountToSendTo){
        Transfer transferInfo = new Transfer();
        transferInfo.setAmountToTransfer(amountToSend);
        transferInfo.setAccountFromId(accountToSendFrom);
        transferInfo.setAccountToId(accountToSendTo);
        transferInfo.setTransferType(transferType);
        transferInfo.setTransferStatus("Pending");
        HttpEntity<Transfer> entity = makeTransferEntity(transferInfo, user.getToken());

        Transfer returnedTransfer = null;
        try {
            returnedTransfer = restTemplate.postForObject(basicAPIUrl + "/transfer", entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }
    //Sends an amount of TE Bucks to the
    public boolean sendBucks(AuthenticatedUser user, int transferId, String transferStatus){
        Transfer requestedTransfer = getTransfer(user, transferId);
        requestedTransfer.setTransferStatus(transferStatus);
        HttpEntity<Transfer> entity = makeTransferEntity(requestedTransfer, user.getToken());
        try {
            restTemplate.put(basicAPIUrl + "/transfer/pay", entity);
            return true;
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return false;
    }
    //Retrieves a specific transfer
    public Transfer getTransfer(AuthenticatedUser user, int transferId){
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(basicAPIUrl + transferId + "/transfer",HttpMethod.GET, makeAuthEntity(user.getToken()), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public void viewUserList(User[] users) {
        System.out.println("\nUsers");
        System.out.println("-------------------------------------------");
        System.out.println("ID          Name");
        System.out.println("-------------------------------------------");
        for (User user : users) {
            System.out.printf("%-12d%s%n", user.getId(), user.getUsername());
        }
        System.out.println("------------------------\n");

    }



    private HttpEntity<Void> makeAuthEntity(String authToken){
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(authToken);
        return new HttpEntity<>(header);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, String authToken){
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(authToken);
        header.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, header);
    }
}
