package com.gabriaum.devroom.domain.inventory.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.domain.inventory.InventoryData;
import com.gabriaum.devroom.domain.service.DiscordService;
import com.gabriaum.devroom.domain.service.InventoryService;
import com.gabriaum.devroom.domain.service.ProductService;
import com.gabriaum.devroom.product.Product;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.stack.ItemBuilder;
import com.gabriaum.devroom.util.stack.ItemSerializer;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.RenderContext;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ConfirmTransactionInventory extends View {
    private final InventoryService service = new InventoryService();
    private final ProductService productService = new ProductService();
    private final DiscordService discordService = new DiscordService();

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        InventoryData data = service.loadInventory("confirm");
        if (data == null) throw new IllegalStateException("ConfirmTransactionInventory[confirm] not found in inventory.yml");

        config.title(data.getTitle());
        config.size(data.getSize());
        config.type(ViewType.CHEST);
        config.cancelOnDrop();
        config.cancelOnClick();
        config.cancelOnDrag();
        config.cancelOnPickup();
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        Map<String, Object> data = (Map<String, Object>) render.getInitialData();
        if (data == null) return;

        Product product = (Product) data.get("product");
        double price = (double) data.get("price");
        ConfigUtil inventoryConfig = MarketMain.getInstance().getInventory();
        String path = "confirm.items.";
        if (inventoryConfig.contains(path + "confirm")) {
            int slot = inventoryConfig.getInt(path + "confirm.slot", 0);
            String material = inventoryConfig.getString(path + "confirm.material", "GREEN_WOOL");
            String name = inventoryConfig.getString(path + "confirm.name", "&aConfirm");
            List<String> lore = inventoryConfig.getStringList(path + "confirm.lore");
            lore.replaceAll(line -> line
                    .replace("{id}", product.getId())
                    .replace("{price}", String.valueOf(price)));
            render.slot(slot, new ItemBuilder(material)
                            .setName(name)
                            .setLore(lore)
                            .build())
                    .onClick(context -> {
                        ConfigUtil messages = MarketMain.getInstance().getMessages();
                        Player player = context.getPlayer();
                        Economy economy = MarketMain.getEconomy();
                        if (economy == null) {
                            MarketMain.sendDebug("Vault economy is not available. Please install Vault and an economy plugin.");
                            return;
                        }

                        double money = economy.getBalance(player);
                        if (money < price) {
                            player.sendMessage(messages.getString("not-enough-money", "You do not have enough money to buy this product.")
                                    .replace("{price}", String.valueOf(price))
                                    .replace("{balance}", String.valueOf(money)));
                            return;
                        }

                        ItemStack item = ItemSerializer.read(product.getSerializedItem());
                        if (item == null) return;

                        ItemMeta meta = item.getItemMeta();
                        Player seller = Bukkit.getPlayer(product.getAnnounceById());
                        if (seller != null)
                            seller.sendMessage(messages.getString("product-sold", "Your product has been sold.")
                                    .replace("{name}", meta != null && meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name())
                                    .replace("{price}", String.valueOf(price))
                                    .replace("{buyer}", player.getName()));

                        productService.buy(player, product, price);
                        player.sendMessage(messages.getString("product-bought", "You have successfully bought the product.")
                                .replace("{name}", meta != null && meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name())
                                .replace("{price}", String.valueOf(price)));
                        discordService.send(product, player, price);
                        player.closeInventory();
                    });
        }

        if (inventoryConfig.contains(path + "cancel")) {
            int slot = inventoryConfig.getInt(path + "cancel.slot", 8);
            String material = inventoryConfig.getString(path + "cancel.material", "RED_WOOL");
            String name = inventoryConfig.getString(path + "cancel.name", "&cCancel");
            List<String> lore = inventoryConfig.getStringList(path + "cancel.lore");
            render.slot(slot, new ItemBuilder(material)
                            .setName(name)
                            .setLore(lore)
                            .build())
                    .onClick(context -> {
                        Player player = context.getPlayer();
                        player.closeInventory();
                        player.sendMessage(MarketMain.getInstance().getMessages().getString("confirm.cancelled", "&cTransaction cancelled."));
                    });
        }
    }
}
