package com.dummycoding.mycrypto.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dummycoding.mycrypto.models.OwnedToken;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface OwnedTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOwnedToken(OwnedToken ownedToken);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOwnedTokens(OwnedToken... tokens);

    @Query("SELECT * FROM OwnedToken")
    Flowable<List<OwnedToken>> getAllOwnedTokens();

    @Query("SELECT * FROM OwnedToken")
    Single<List<OwnedToken>> getAllOwnedTokensSingle();

    @Delete
    Completable delete(OwnedToken ownedToken);
}
