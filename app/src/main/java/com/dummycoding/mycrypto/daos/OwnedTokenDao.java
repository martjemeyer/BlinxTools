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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOwnedTokens(List<OwnedToken> tokens);

    @Query("SELECT * FROM OwnedToken ORDER BY orderedIndex")
    Flowable<List<OwnedToken>> getAllOwnedTokens();

    @Query("SELECT * FROM OwnedToken ORDER BY orderedIndex")
    Single<List<OwnedToken>> getAllOwnedTokensSingle();

    @Query("UPDATE OwnedToken SET orderedIndex=:price WHERE id = :id")
    Completable update(int id, int price);

    @Delete
    Completable delete(OwnedToken ownedToken);
}
