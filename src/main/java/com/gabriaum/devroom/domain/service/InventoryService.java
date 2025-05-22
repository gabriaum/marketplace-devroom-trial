package com.gabriaum.devroom.domain.service;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.domain.inventory.InventoryData;
import com.gabriaum.devroom.domain.inventory.annotation.ConfigColumn;
import com.gabriaum.devroom.util.ConfigUtil;

import java.lang.reflect.Field;
import java.util.List;

public class InventoryService {

    public InventoryData loadInventory(String inventoryKey) {
        ConfigUtil inventoryConfig = MarketMain.getInstance().getInventory();
        InventoryData inventory = new InventoryData();

        for (Field field : InventoryData.class.getDeclaredFields()) {
            ConfigColumn annotation = field.getAnnotation(ConfigColumn.class);
            if (annotation == null) continue;

            String configKey = annotation.fieldName();
            field.setAccessible(true);
            Object value = inventoryConfig.get(inventoryKey + "." + configKey);
            if (value == null) continue;

            try {
                Class<?> fieldType = field.getType();
                if (fieldType == String[].class && value instanceof List<?> list) {
                    if (!list.isEmpty() && list.get(0) instanceof String) {
                        String[] array = list.toArray(new String[0]);
                        field.set(inventory, array);
                    }
                } else if (fieldType == String.class && value instanceof String) {
                    field.set(inventory, value);
                } else if (fieldType == int.class && value instanceof Number) {
                    field.set(inventory, ((Number) value).intValue());
                } else {
                    MarketMain.sendDebug("Untreated type: " + fieldType.getSimpleName() + " for field " + field.getName());
                }
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        return inventory;
    }
}