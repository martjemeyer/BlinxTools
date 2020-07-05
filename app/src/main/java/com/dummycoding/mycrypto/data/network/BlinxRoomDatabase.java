package com.dummycoding.mycrypto.data.network;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dummycoding.mycrypto.daos.BlinxResultDao;
import com.dummycoding.mycrypto.daos.CoinDeskCurrencyDao;
import com.dummycoding.mycrypto.daos.OwnedTokenDao;
import com.dummycoding.mycrypto.models.OwnedToken;
import com.dummycoding.mycrypto.models.bitblinx.Result;
import com.dummycoding.mycrypto.models.coindesk.Currency;

@Database(entities = {Result.class, Currency.class, OwnedToken.class}, version = 2, exportSchema = false)
public abstract class BlinxRoomDatabase extends androidx.room.RoomDatabase {

    public abstract BlinxResultDao blinxResultDao();
    public abstract CoinDeskCurrencyDao coinDeskCurrencyDao();
    public abstract OwnedTokenDao ownedTokenDao();

    private static volatile BlinxRoomDatabase INSTANCE;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE OwnedToken "
                    + " ADD COLUMN orderedIndex INTEGER NOT NULL DEFAULT 0 ");
        }
    };

    static BlinxRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BlinxRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BlinxRoomDatabase.class, "mycrypto_database")
                            .addMigrations(MIGRATION_1_2)
                            //.fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
