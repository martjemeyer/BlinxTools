package com.dummycoding.blinxtools.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dummycoding.blinxtools.models.bitblinx.Result;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface BlinxResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertResults(List<Result> result);

    @Query("SELECT * FROM Result")
    Flowable<List<Result>> getAllResults();

    @Query("SELECT symbol FROM Result WHERE symbol LIKE '%/BTC%' " +
            "ORDER BY CASE WHEN symbol LIKE 'GT%' THEN 0 ELSE 1 END, symbol DESC")
    Single<List<String>> getAllBtcPairs();

    @Query("SELECT * FROM Result WHERE symbol IN (:symbol)")
    Single<List<Result>> getTokenBySymbol(String symbol);
}
