package com.gabriaum.devroom.command.handler.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.Account;
import com.gabriaum.devroom.command.handler.ArgumentHandler;
import com.gabriaum.devroom.product.Product;
import com.gabriaum.devroom.util.command.CommandArgs;
import com.gabriaum.devroom.util.stack.ItemSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AnnounceArgumentHandler implements ArgumentHandler {
    @Override
    public String getName() {
        return "announce";
    }

    @Override
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Account account = Account.getAccount(player.getUniqueId());
        if (account == null) {
            player.sendMessage(getMessages().getString("account_not_found", "§cAccount not found!"));
            return;
        }

        String[] args = commandArgs.getArgs();
        if (args.length < 2) {
            player.sendMessage("§cUsage: /" + commandArgs.getLabel() + " announce <price>");
            return;
        }

        String priceFormat = args[1];
        if (!priceFormat.matches("[0-9]+")) {
            player.sendMessage(getMessages().getString("command.announce.invalid_price_format", "§cInvalid price format!"));
            return;
        }

        int price = Integer.parseInt(priceFormat);
        if (price <= 0) {
            player.sendMessage(getMessages().getString("command.announce.invalid_price", "§cPrice must be greater than 0!"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(getMessages().getString("command.announce.no_item", "§cYou must hold an item in your hand to announce!"));
            return;
        }

        Product product = Product.builder()
                .announceById(player.getUniqueId())
                .announceByName(player.getName())
                .serializedItem(ItemSerializer.write(item))
                .price(price)
                .amount(item.getAmount())
                .build();
        player.getInventory().setItemInMainHand(null);
        player.updateInventory();
        MarketMain.getInstance().getProductController().add(product);
        player.sendMessage(getMessages().getString("command.announce.success", "§aItem announced successfully!"));
    }
}
