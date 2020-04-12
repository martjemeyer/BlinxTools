package com.dummycoding.blinxtools.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dummycoding.blinxtools.models.OwnedToken;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface OwnedTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOwnedToken(OwnedToken ownedToken);

    @Query("SELECT * FROM OwnedToken")
    Flowable<List<OwnedToken>> getAllOwnedTokens();

    @Delete
    Completable delete(OwnedToken ownedToken);
}
