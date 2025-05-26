package com.gabriaum.devroom.domain.inventory.impl;

import com.gabriaum.devroom.domain.inventory.InventoryData;
import com.gabriaum.devroom.domain.service.InventoryService;
import com.gabriaum.devroom.product.Product;
import com.gabriaum.devroom.product.attribute.ProductAttribute;
import com.gabriaum.devroom.util.stack.ItemBuilder;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultMarketInventory extends View {
    private final InventoryService service = new InventoryService();
    private final State<Pagination> paginationState = lazyPaginationState(
            context -> {
                Map<String, Object> data = (Map<String, Object>) context.getInitialData();
                if (data == null) return new ArrayList<>();
                return (ArrayList<Product>) data.get("items");
            }, (context, builder, index, product) -> {
                ProductAttribute attribute = product.getAttribute();
                ItemBuilder itemBuilder = new ItemBuilder(attribute.getMaterial());
                itemBuilder.setName(Objects.requireNonNull(service.getInventoryConfig().getString("inventory-item-model.name"))
                        .replace("{name}", attribute.getName()));
                List<String> lore = attribute.getLore();
                lore.add("");
                lore.add(Objects.requireNonNull(service.getInventoryConfig().getString("inventory-item-model.price-format"))
                        .replace("{name}", attribute.getName())
                        .replace("{price}", String.valueOf(product.getPrice())));
                lore.add("");
                lore.add(Objects.requireNonNull(service.getInventoryConfig().getString("inventory-item-model.buy-format")));
                itemBuilder.setLore(lore);
                builder.withItem(itemBuilder.build());
            });

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        InventoryData data = service.loadInventory("default-market");
        if (data == null)
            throw new IllegalStateException("Inventory data for 'default-market' not found.");
        System.out.println("Loading inventory data: " + data.getTitle());
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

    }
}
