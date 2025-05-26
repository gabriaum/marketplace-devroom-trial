package com.gabriaum.devroom.command.handler.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.command.handler.ArgumentHandler;
import com.gabriaum.devroom.domain.inventory.impl.DefaultMarketInventory;
import com.gabriaum.devroom.util.command.CommandArgs;
import me.devnatan.inventoryframework.View;
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
        String[] args = commandArgs.getArgs();
        if (args.length < 1) {
            player.sendMessage(getMessages().getString("command.open.default-usage", "§cUsage: /" + commandArgs.getLabel() + " open <normal, dark>")
                    .replace("{label}", commandArgs.getLabel()));
            return;
        }

        try {
            Class<? extends View> view = DefaultMarketInventory.class;

            MarketMain.getInstance().getViewFrame().open(view.newInstance().getClass(), player,
                    Map.of("items", MarketMain.getInstance().getProductController()));
        } catch (InstantiationException | IllegalAccessException e) {
            player.sendMessage(getMessages().getString("command.open.error", "§cAn error occurred while opening the inventory!"));
            e.printStackTrace();
        }
    }
}