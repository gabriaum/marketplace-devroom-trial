package com.gabriaum.devroom.domain.inventory;

import com.gabriaum.devroom.domain.inventory.annotation.ConfigColumn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryData {
    @ConfigColumn(fieldName = "name")
    private String title;
    @ConfigColumn(fieldName = "size")
    private int size;
    @ConfigColumn(fieldName = "layout")
    private String[] layout;
}