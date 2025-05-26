package com.gabriaum.devroom.account;

import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.product.Product;
import lombok.Getter;

import java.util.*;

@Getter
public class Account {
    protected final UUID uniqueId;
    protected final String name;

    private final List<Product> advertisedProducts;
    private final Set<ProductSold> purchasedProducts;

    public Account(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.advertisedProducts = new ArrayList<>();
        this.purchasedProducts = new HashSet<>();
    }

    protected void save(String... fields) {
        for (String field : fields) {

        }
    }
}