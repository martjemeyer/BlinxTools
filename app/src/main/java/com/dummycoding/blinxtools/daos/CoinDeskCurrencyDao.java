package com.dummycoding.blinxtools.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dummycoding.blinxtools.models.bitblinx.Result;
import com.dummycoding.blinxtools.models.coindesk.Currency;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface CoinDeskCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCurrencies(List<Currency> currencies);

    @Query("SELECT * FROM Currency")
    Flowable<List<Currency>> getAllCurrencies();

    @Query("SELECT currency FROM Currency ORDER BY country")
    Flowable<List<String>> getAllCurrencyStrings();

    @Query("SELECT country FROM Currency ORDER BY country")
    Flowable<List<String>> getAllCountryStrings();
}
