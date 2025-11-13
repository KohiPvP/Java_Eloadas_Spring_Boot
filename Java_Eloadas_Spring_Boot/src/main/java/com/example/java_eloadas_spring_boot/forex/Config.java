package com.example.java_eloadas_spring_boot.forex;
import com.oanda.v20.account.AccountID;

public class Config {
    private Config() {}
    public static final String URL = "https://api-fxpractice.oanda.com";
    public static final AccountID ACCOUNTID = new AccountID("101-004-37632674-001");
    public static final String TOKEN = "86c08d6e13afc3b5edea2364fd90794a-4787f6a2003c8ebb416b894e1db80112";

}
