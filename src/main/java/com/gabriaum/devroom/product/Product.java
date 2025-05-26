package com.gabriaum.devroom.product;

import com.gabriaum.devroom.product.attribute.ProductAttribute;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

@Getter
@Setter
@Builder
public class Product {
    private final String id = RandomStringUtils.randomAlphanumeric(8);
    private UUID announceById;
    private String announceByName;
    private ProductAttribute attribute;
    private int price;
    private int amount;
}