package com.dummycoding.mycrypto.adapters;

import com.dummycoding.mycrypto.models.OwnedToken;

import java.util.List;

public interface OwnedTokensAdapterCallback {
    void updateOwnedToken(OwnedToken ownedToken);
    void updateOwnedTokensOrder(List<OwnedToken> ownedTokens);
}
