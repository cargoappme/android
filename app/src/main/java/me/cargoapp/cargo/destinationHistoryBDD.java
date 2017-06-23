package me.cargoapp.cargo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mathieu on 14/06/2017.
 */

public class destinationHistoryBDD extends SQLiteOpenHelper {
    private static final String TABLE_DESTINATIONS = "table_destinations";
    private static final String COL_ID = "ID";
    private static final String COL_ADRESS = "ADRESS";
    private static final String COL_LON = "Longitude";
    private static final String COL_LAT = "Latitude";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_DESTINATIONS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ADRESS + " TEXT NOT NULL, "
            + COL_LON + " DOUBLE NOT NULL,"+ COL_LAT + " DOUBLE NOT NULL);";

    public destinationHistoryBDD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_DESTINATIONS + ";");
        onCreate(db);
    }
}
