package com.gabriaum.devroom.product;

import com.gabriaum.devroom.product.attribute.ProductAttribute;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class Product {
    private UUID announceById;
    private String announceByName;
    private ProductAttribute attribute;
    private int price;
    private int amount;
}