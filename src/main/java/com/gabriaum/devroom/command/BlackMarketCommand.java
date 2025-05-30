package com.gabriaum.devroom.command;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.domain.inventory.impl.BlackMarketInventory;
import com.gabriaum.devroom.util.command.Command;
import com.gabriaum.devroom.util.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class BlackMarketCommand {

    @Command(name = "blackmarket", permission = " marketplace.blackmarket", inGameOnly = true)
    public void blackMarketCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        MarketMain.getInstance().getViewFrame().open(BlackMarketInventory.class, player, Map.of("items", new ArrayList<>(MarketMain.getInstance().getProductController()
                .stream().filter(product -> !product.getAnnounceById().equals(player.getUniqueId()))
                .toList())));
    }
}
