package com.gabriaum.devroom.account;

import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.product.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Account {
    protected final UUID uniqueId;
    protected final String name;

    private final List<Product> advertisedProducts = new LinkedList<>();
    private final Set<ProductSold> purchasedProducts = new HashSet<>();

    protected void save(String... fields) {
        for (String field : fields) {

        }
    }
}