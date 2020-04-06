package com.dummycoding.blinxtools.data.network;

import io.realm.Realm;

public class RealmManager {
    private final ThreadLocal<Realm> localRealm = new ThreadLocal<>();

    public Realm openLocalInstance() {
        Realm realm = Realm.getDefaultInstance();
        if(localRealm.get() == null) {
            localRealm.set(realm);
        }
        return realm;
    }

    public Realm getLocalInstance() {
        Realm realm = localRealm.get();
        if(realm == null) {
            throw new IllegalStateException("No open Realms were found on this thread.");
        }
        return realm;
    }

    public void closeLocalInstance() {
        Realm realm = localRealm.get();
        if (realm == null) {
            throw new IllegalStateException(
                    "Cannot close a Realm that is not open.");
        }
        realm.close();
        if (Realm.getLocalInstanceCount(Realm.getDefaultConfiguration()) <= 0) {
            localRealm.set(null);
        }
    }
}
