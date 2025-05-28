package com.gabriaum.devroom.domain.inventory.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.domain.inventory.InventoryData;
import com.gabriaum.devroom.domain.service.InventoryService;
import com.gabriaum.devroom.domain.service.ProductService;
import com.gabriaum.devroom.product.Product;
import com.gabriaum.devroom.util.ConfigUtil;
import com.gabriaum.devroom.util.stack.ItemBuilder;
import com.gabriaum.devroom.util.stack.ItemSerializer;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BlackMarketInventory extends View {
    private final InventoryService service = new InventoryService();
    private final ProductService productService = new ProductService();
    private final State<Pagination> paginationState = lazyPaginationState(
            context -> {
                Map<String, Object> data = (Map<String, Object>) context.getInitialData();
                if (data == null) return new ArrayList<>();
                return (ArrayList<Product>) data.get("items");
            }, (context, builder, index, product) -> {
                ItemStack item = ItemSerializer.read(product.getSerializedItem());
                if (item == null) return;

                ItemMeta meta = item.getItemMeta();
                ItemBuilder itemBuilder = new ItemBuilder(item.getType());
                itemBuilder.setName(Objects.requireNonNull(service.getInventoryConfig().getString("inventory-item-model.name"))
                        .replace("{name}", meta != null && meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name()));

                List<String> lore = meta != null && meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add("");
                lore.add(Objects.requireNonNull(service.getInventoryConfig().getString("inventory-item-model.price-format"))
                        .replace("{name}", meta != null && meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name())
                        .replace("{price}", String.valueOf(product.getPrice() / 2)));
                lore.add("");
                lore.add(Objects.requireNonNull(service.getInventoryConfig().getString("inventory-item-model.buy-format")));
                itemBuilder.setLore(lore);
                itemBuilder.setAmount(item.getAmount());
                itemBuilder.setDurability(item.getDurability());
                builder.withItem(itemBuilder.build())
                        .onClick(slotClickContext -> {
                            double price = product.getPrice() / 2;
                            context.openForPlayer(ConfirmTransactionInventory.class, Map.of(
                                    "product", product,
                                    "price", price
                            ));
                        });
            });

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        InventoryData data = service.loadInventory("black-market");
        config.title(data.getTitle());
        config.size(data.getSize());
        config.type(ViewType.CHEST);
        config.layout(data.getLayout());
        config.cancelOnDrop();
        config.cancelOnClick();
        config.cancelOnDrag();
        config.cancelOnPickup();
        config.scheduleUpdate(10L);
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        ConfigUtil inventoryConfig = MarketMain.getInstance().getInventory();
        Map<String, Object> initialData = (Map<String, Object>) render.getInitialData();
        if (initialData == null) return;

        List<Product> products = (List<Product>) initialData.get("items");
        if (products.isEmpty()) {
            if (inventoryConfig.contains("black-market.items.empty")) {
                String path = "black-market.items.empty.";
                int slot = inventoryConfig.getInt(path + "slot", 0);
                Material material = Material.getMaterial(inventoryConfig.getString(path + "material", "BARRIER"));
                String name = inventoryConfig.getString(path + "name", "No products available");
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
            if (inventoryConfig.contains("black-market.items.next-page")) {
                String path = inventoryConfig.getString("black-market.items.next-page.");
                int slot = inventoryConfig.getInt(path + "slot", 1);
                Material material = Material.getMaterial(inventoryConfig.getString(path + "material", "ARROW"));
                String name = inventoryConfig.getString(path + "name", "&aNext Page");
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
            if (inventoryConfig.contains("black-market.items.previous-page")) {
                String path = inventoryConfig.getString("black-market.items.previous-page.");
                int slot = inventoryConfig.getInt(path + "slot", 0);
                Material material = Material.getMaterial(inventoryConfig.getString(path + "material", "ARROW"));
                String name = inventoryConfig.getString(path + "name", "&aPrevious Page");
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