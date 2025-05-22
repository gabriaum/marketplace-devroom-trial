package com.gabriaum.devroom.domain.inventory.impl;

import com.gabriaum.devroom.domain.inventory.InventoryData;
import com.gabriaum.devroom.domain.service.InventoryService;
import com.gabriaum.devroom.product.Product;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class InventoryDataImpl extends View {
    private final InventoryService service = new InventoryService();
    private final InventoryData data = service.loadInventory();
    private final State<Pagination> paginationState = lazyPaginationState(
            context -> {
                Map<String, Object> data = (Map<String, Object>) context.getInitialData();
                if (data == null) return new ArrayList<>();
                return (ArrayList<Product>) data.get("items");
            }, (context, builder, index, product) -> {

            });

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
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
