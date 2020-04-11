package com.dummycoding.blinxtools.data.network;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.dummycoding.blinxtools.daos.BlinxResultDao;
import com.dummycoding.blinxtools.daos.CoinDeskCurrencyDao;
import com.dummycoding.blinxtools.daos.OwnedTokenDao;
import com.dummycoding.blinxtools.models.OwnedToken;
import com.dummycoding.blinxtools.models.bitblinx.Result;
import com.dummycoding.blinxtools.models.coindesk.Currency;

@Database(entities = {Result.class, Currency.class, OwnedToken.class}, version = 1, exportSchema = false)
public abstract class BlinxRoomDatabase extends androidx.room.RoomDatabase {

    public abstract BlinxResultDao blinxResultDao();
    public abstract CoinDeskCurrencyDao coinDeskCurrencyDao();
    public abstract OwnedTokenDao ownedTokenDao();

    private static volatile BlinxRoomDatabase INSTANCE;

    public static BlinxRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BlinxRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BlinxRoomDatabase.class, "blinxtools_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
