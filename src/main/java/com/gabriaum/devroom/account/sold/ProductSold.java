package com.gabriaum.devroom.account.sold;

import com.gabriaum.devroom.product.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ProductSold {
    private final Product product;
    private final UUID purchasedById;
    private final String purchaseByName;
    private final int price;
}
