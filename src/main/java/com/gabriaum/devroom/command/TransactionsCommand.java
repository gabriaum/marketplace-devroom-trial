package com.gabriaum.devroom.command;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.domain.inventory.impl.TransactionInventory;
import com.gabriaum.devroom.util.command.Command;
import com.gabriaum.devroom.util.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionsCommand {

    @Command(name = "transactions", permission = "marketplace.history", inGameOnly = true)
    public void transactionsCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        List<ProductSold> transactions = new ArrayList<>(MarketMain.getInstance().getTransactionData().getAllSoldProductsBy(player.getUniqueId()));
        MarketMain.getInstance().getViewFrame().open(TransactionInventory.class, player, Map.of("items", transactions));
    }
}
