package com.gabriaum.devroom.backend.data;

import com.gabriaum.devroom.account.Account;

import java.util.UUID;

public interface AccountData {
    Account register(UUID uniqueId, String name);
    Account getAccount(UUID uniqueId);
    Account getAccount(String name);
    void update(Account account, String fieldName);
}