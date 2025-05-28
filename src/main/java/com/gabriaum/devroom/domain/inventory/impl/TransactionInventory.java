package com.gabriaum.devroom.domain.inventory.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.backend.data.TransactionData;
import com.gabriaum.devroom.domain.inventory.InventoryData;
import com.gabriaum.devroom.domain.service.InventoryService;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.stack.ItemBuilder;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionInventory extends View {
    private final InventoryService service = new InventoryService();
    private final TransactionData transactionData = MarketMain.getInstance().getTransactionData();
    private final State<Pagination> paginationState = lazyPaginationState(
            context -> {
                Map<String, Object> data = (Map<String, Object>) context.getInitialData();
                if (data == null) return new ArrayList<>();
                return (ArrayList<ProductSold>) data.get("items");
            }, (context, builder, index, sold) -> {
                ConfigUtil inventoryConfig = MarketMain.getInstance().getInventory();
                String path = "transaction.items.model.";
                Material material = Material.getMaterial(inventoryConfig.getString(path + "material", "PAPER"));
                String name = inventoryConfig.getString(path + "name", "§a" + sold.getProduct().getId())
                        .replace("{id}", sold.getProduct().getId());
                List<String> lore = inventoryConfig.getStringList(path + "lore");
                lore.replaceAll(line -> line
                        .replace("{date}", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(sold.getSoldAt())))
                        .replace("{price}", String.valueOf(sold.getPrice()))
                        .replace("{purchased_by_name}", sold.getPurchaseByName())
                        .replace("{seller_by_name}", sold.getProduct().getAnnounceByName()));
                builder.withItem(new ItemBuilder(material)
                        .setName(name)
                        .setLore(lore)
                        .build());
            });

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        InventoryData data = service.loadInventory("transaction");
        System.out.println("GSON: " + MarketMain.GSON.toJson(data));
        config.title(data.getTitle());
        config.size(data.getSize());
        config.type(ViewType.CHEST);
        config.layout(data.getLayout());
        config.cancelOnDrop();
        config.cancelOnClick();
        config.cancelOnDrag();
        config.cancelOnPickup();
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        ConfigUtil inventoryConfig = MarketMain.getInstance().getInventory();
        if (transactionData.getAllSoldProductsBy(render.getPlayer().getUniqueId()).isEmpty()) {
            if (inventoryConfig.contains("transaction.items.empty")) {
                String path = "transaction.items.empty.";
                int slot = inventoryConfig.getInt(path + "slot", 0);
                Material material = Material.getMaterial(inventoryConfig.getString(path + "material", "BARRIER"));
                String name = inventoryConfig.getString(path + "name", "§cNo transactions found");
                List<String> lore = inventoryConfig.getStringList(path + "lore");
                render.slot(slot, new ItemBuilder(material)
                        .setName(name)
                        .setLore(lore)
                        .build());
            }

            return;
        }

        Pagination pagination = paginationState.get(render);
        if (pagination == null) return;

        if (pagination.canAdvance()) {
            if (inventoryConfig.contains("transaction.items.next-page")) {
                String path = inventoryConfig.getString("transaction.items.next-page.");
                int slot = inventoryConfig.getInt(path + "slot", 1);
                Material material = Material.getMaterial(inventoryConfig.getString(path + "material", "ARROW"));
                String name = inventoryConfig.getString(path + "name", "§aNext Page");
                List<String> lore = inventoryConfig.getStringList(path + "lore");
                render.slot(slot, new ItemBuilder(material)
                                .setName(name)
                                .setLore(lore)
                                .build())
                        .onClick(context -> {
                            if (pagination.canAdvance())
                                pagination.advance();
                        });
            }
        }

        if (pagination.canBack()) {
            if (inventoryConfig.contains("transaction.items.previous-page")) {
                String path = inventoryConfig.getString("transaction.items.previous-page.");
                int slot = inventoryConfig.getInt(path + "slot", 0);
                Material material = Material.getMaterial(inventoryConfig.getString(path + "material", "ARROW"));
                String name = inventoryConfig.getString(path + "name", "§aPrevious Page");
                List<String> lore = inventoryConfig.getStringList(path + "lore");
                render.slot(slot, new ItemBuilder(material)
                                .setName(name)
                                .setLore(lore)
                                .build())
                        .onClick(context -> {
                            if (pagination.canBack())
                                pagination.back();
                        });
            }
        }
    }
}