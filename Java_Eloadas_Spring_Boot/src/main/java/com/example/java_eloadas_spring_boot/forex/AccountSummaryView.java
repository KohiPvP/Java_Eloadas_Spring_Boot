package com.example.java_eloadas_spring_boot.forex;

import com.oanda.v20.account.AccountSummary;

import java.math.BigDecimal;

public class AccountSummaryView {
    private String accountId;
    private String currency;
    private BigDecimal balance;
    private BigDecimal unrealizedPL;
    private BigDecimal nav;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getUnrealizedPL() {
        return unrealizedPL;
    }

    public void setUnrealizedPL(BigDecimal unrealizedPL) {
        this.unrealizedPL = unrealizedPL;
    }

    public BigDecimal getNav() {
        return nav;
    }

    public void setNav(BigDecimal nav) {
        this.nav = nav;
    }

    public static AccountSummaryView fromApi(AccountSummary api) {
        AccountSummaryView view = new AccountSummaryView();
        view.setAccountId(String.valueOf(api.getId()));
        view.setCurrency(String.valueOf(api.getCurrency()));
        view.setBalance(api.getBalance().bigDecimalValue());
        view.setUnrealizedPL(api.getUnrealizedPL().bigDecimalValue());
        view.setNav(api.getNAV().bigDecimalValue());

        return view;
    }

}
