package com.gabriaum.devroom.account;

import com.gabriaum.devroom.MarketMain;
import lombok.Getter;

import java.util.*;

@Getter
public class Account {
    protected final UUID uniqueId;
    protected final String name;

    private double grossProfit;

    public Account(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.grossProfit = 0;
    }

    public void addGrossProfit(double amount) {
        this.grossProfit += amount;
        save("grossProfit");
    }

    protected void save(String... fields) {
        for (String field : fields) {
            MarketMain.getInstance().getAccountData().update(this, field);
        }
    }

    public static Account getAccount(UUID uniqueId) {
        return MarketMain.getInstance().getAccountController().get(uniqueId);
    }
}