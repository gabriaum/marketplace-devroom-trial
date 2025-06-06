package com.gabriaum.devroom.product;

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
    private String serializedItem;
    private double price;
    private int amount;
    private final long createdAt = System.currentTimeMillis();
}