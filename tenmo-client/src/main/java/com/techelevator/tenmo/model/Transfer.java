package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Transfer {

    private int id;
    private String transferType;
    private String transferStatus;
    private int accountFromId;
    private int accountToId;
    private BigDecimal amountToTransfer;

    public Transfer(){}

    public Transfer(String transferType, String transferStatus, int accountFromId, int accountToId, BigDecimal amountToTransfer) {
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amountToTransfer = amountToTransfer;
    }

    public Transfer(int id, String transferType, String transferStatus, int accountFromId, int accountToId, BigDecimal amountToTransfer) {
        this.id = id;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amountToTransfer = amountToTransfer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmountToTransfer() {
        return amountToTransfer;
    }

    public void setAmountToTransfer(BigDecimal amountToTransfer) {
        this.amountToTransfer = amountToTransfer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return id == transfer.id && accountFromId == transfer.accountFromId && accountToId == transfer.accountToId && Objects.equals(transferType, transfer.transferType) && Objects.equals(transferStatus, transfer.transferStatus) && Objects.equals(amountToTransfer, transfer.amountToTransfer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transferType, transferStatus, accountFromId, accountToId, amountToTransfer);
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", transferType='" + transferType + '\'' +
                ", transferStatus='" + transferStatus + '\'' +
                ", accountFromId=" + accountFromId +
                ", accountToId=" + accountToId +
                ", amountToTransfer=" + amountToTransfer +
                '}';
    }

    public String printPretty(){
        return String.format("|ID|%-6d|Transfer Type|%7s|Transfer Status|%7s|Amount Of Transfer|%.2f", getId(), getTransferType(), getTransferStatus(), getAmountToTransfer().doubleValue());
    }
}
