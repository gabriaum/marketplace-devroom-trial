package com.gabriaum.devroom.command.handler.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.command.handler.ArgumentHandler;
import com.gabriaum.devroom.domain.inventory.impl.DefaultMarketInventory;
import com.gabriaum.devroom.util.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.Map;

public class OpenArgumentHandler implements ArgumentHandler {
    @Override
    public String getName() {
        return "open";
    }

    @Override
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        MarketMain.getInstance().getViewFrame().open(DefaultMarketInventory.class, player,
                Map.of("items", MarketMain.getInstance().getProductController()));
    }
}