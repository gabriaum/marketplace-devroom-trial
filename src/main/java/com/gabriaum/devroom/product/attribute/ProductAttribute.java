package com.gabriaum.devroom.product.attribute;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ProductAttribute {
    private Material material;
    private String name;
    private List<String> lore;
    private NBTCompound tag;
    private Map<Enchantment, Integer> enchantments;
}
