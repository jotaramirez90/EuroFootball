package com.android.jota.eurofootball.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.jota.eurofootball.data.MatchContract.*;

public class MatchDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "match.db";

    public MatchDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LEAGUE_TABLE = "CREATE TABLE " + LeagueEntry.TABLE_NAME + " (" +
                LeagueEntry._ID + " INTEGER PRIMARY KEY," +
                LeagueEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                LeagueEntry.COLUMN_LEAGUE_ID + " INTEGER UNIQUE NOT NULL, " +
                LeagueEntry.COLUMN_YEAR + " INTEGER NOT NULL, " +
                "UNIQUE (" + LeagueEntry.COLUMN_LEAGUE_ID + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_MATCH_TABLE = "CREATE TABLE " + MatchEntry.TABLE_NAME + " (" +
                MatchEntry._ID + " INTEGER PRIMARY KEY," +
                MatchEntry.COLUMN_MATCH_ID + " INTEGER UNIQUE NOT NULL," +
                MatchEntry.COLUMN_LOCAL_TEAM + " INTEGER NOT NULL, " +
                MatchEntry.COLUMN_VISITOR_TEAM + " INTEGER NOT NULL, " +
                MatchEntry.COLUMN_LOCAL_GOALS + " TEXT NOT NULL, " +
                MatchEntry.COLUMN_VISITOR_GOALS + " TEXT NOT NULL," +
                MatchEntry.COLUMN_LOCAL_SHIELD + " TEXT NOT NULL," +
                MatchEntry.COLUMN_VISITOR_SHIELD + " TEXT NOT NULL," +
                MatchEntry.COLUMN_ROUND + " INTEGER NOT NULL," +
                MatchEntry.COLUMN_DATE + " TEXT NOT NULL," +
                MatchEntry.COLUMN_HOUR + " INTEGER NOT NULL," +
                MatchEntry.COLUMN_MINUTE + " INTEGER NOT NULL," +
                MatchEntry.COLUMN_LEAGUE_ID + " INTEGER NOT NULL," +
                "UNIQUE (" + MatchEntry.COLUMN_MATCH_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LEAGUE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MATCH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LeagueEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MatchEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
