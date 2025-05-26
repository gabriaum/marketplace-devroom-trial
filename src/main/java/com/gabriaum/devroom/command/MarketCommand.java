package com.gabriaum.devroom.command;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.domain.inventory.impl.DefaultMarketInventory;
import com.gabriaum.devroom.util.command.Command;
import com.gabriaum.devroom.util.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.Map;

public class MarketCommand {

    @Command(name = "marketplace", permission = "marketplace.view", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        MarketMain.getInstance().getViewFrame().open(DefaultMarketInventory.class, player, Map.of("items", MarketMain.getInstance().getProductController()));
    }
}
