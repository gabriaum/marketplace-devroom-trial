package com.gabriaum.devroom.domain.service;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.Account;
import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.product.Product;
import com.gabriaum.devroom.util.stack.ItemSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public class ProductService {

    public void buy(Player player, Product product, double buyPrice) {
        Account account = Account.getAccount(player.getUniqueId());
        if (account == null) throw new IllegalStateException("Account not found for player: " + player.getName());

        CompletableFuture.runAsync(() -> {
            Account sellerAccount = Account.getAccount(product.getAnnounceById());
            if (sellerAccount == null)
                sellerAccount = MarketMain.getInstance().getAccountData().getAccount(player.getUniqueId());

            ProductSold sold = new ProductSold(product, player.getUniqueId(), player.getName(), buyPrice);
            account.addPurchasedProduct(sold);
            sellerAccount.addPurchasedProduct(sold);
            sellerAccount.removeAdvertisedProduct(product);
            MarketMain.getInstance().getProductData().unregister(product);
            MarketMain.getInstance().getProductController().removeIf(p -> p.getId().equals(product.getId()));
        });

        sendItemTo(player, product);
    }

    protected void sendItemTo(Player player, Product product) {
        ItemStack item = ItemSerializer.read(product.getSerializedItem());
        if (item == null) {
            MarketMain.sendDebug("Failed to deserialize item for product: " + product.getId());
            return;
        }

        boolean drop = false;
        if (player.getInventory().firstEmpty() == -1) {
            drop = true;
            MarketMain.sendDebug("Inventory is full, dropping item for player: " + player.getName());
        }

        if (drop) player.getWorld().dropItem(player.getLocation(), item);
        else player.getInventory().addItem(item);
    }
}