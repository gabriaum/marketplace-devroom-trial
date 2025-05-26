package com.gabriaum.devroom.command;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.Account;
import com.gabriaum.devroom.product.Product;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.command.Command;
import com.gabriaum.devroom.util.command.CommandArgs;
import com.gabriaum.devroom.util.stack.ItemSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand {

    @Command(name = "sell", permission = "marketplace.sell", inGameOnly = true)
    public void sellCommand(CommandArgs commandArgs) {
        ConfigUtil messages = MarketMain.getInstance().getMessages();
        Player player = commandArgs.getPlayer();
        Account account = Account.getAccount(player.getUniqueId());
        if (account == null) {
            player.sendMessage(messages.getString("account_not_found", "§cAccount not found!"));
            return;
        }

        String[] args = commandArgs.getArgs();
        if (args.length < 1) {
            player.sendMessage(messages.getString("command.sell.default-usage", "§cUsage: /sell <price>")
                    .replace("{label}", commandArgs.getLabel()));
            return;
        }

        String priceFormat = args[0];
        if (!priceFormat.matches("[0-9]+")) {
            player.sendMessage(messages.getString("command.sell.invalid_price_format", "§cInvalid price format!"));
            return;
        }

        int price = Integer.parseInt(priceFormat);
        if (price <= 0) {
            player.sendMessage(messages.getString("command.sell.invalid_price", "§cPrice must be greater than 0!"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(messages.getString("command.sell.no_item", "§cYou must hold an item in your hand to announce!"));
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
        player.sendMessage(messages.getString("command.sell.success", "§aItem announced successfully!"));
    }
}