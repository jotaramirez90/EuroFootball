package com.android.jota.eurofootball.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.jota.eurofootball.Utility;

public class MatchProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MatchDbHelper mOpenHelper;

    private static final int LEAGUE = 10;
    private static final int MATCH = 20;
    private static final int MATCH_DATE = 21;

    private static final String sMatchLeagueSelection =
            MatchContract.MatchEntry.TABLE_NAME +
                    "." + MatchContract.MatchEntry.COLUMN_LEAGUE_ID + " = ?";

    private static final String sMatchRoundSelection =
            MatchContract.MatchEntry.TABLE_NAME +
                    "." + MatchContract.MatchEntry.COLUMN_ROUND + " = ?";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MatchContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MatchContract.PATH_LEAGUE, LEAGUE);
        matcher.addURI(authority, MatchContract.PATH_MATCH, MATCH);
        matcher.addURI(authority, MatchContract.PATH_MATCH + "/#", MATCH_DATE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MatchDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "league"
            case LEAGUE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MatchContract.LeagueEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "match"
            case MATCH: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MatchContract.MatchEntry.TABLE_NAME,
                        projection,
                        sMatchLeagueSelection,
                        new String[]{Utility.getPreferredLeague(getContext())},
                        null,
                        null,
                        null
                );
                break;
            }
            //"match data"
            case MATCH_DATE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MatchContract.MatchEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LEAGUE:
                return MatchContract.LeagueEntry.CONTENT_ITEM_TYPE;
            case MATCH:
                return MatchContract.MatchEntry.CONTENT_TYPE;
            case MATCH_DATE:
                return MatchContract.MatchEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case LEAGUE: {
                long _id = db.insert(MatchContract.LeagueEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MatchContract.LeagueEntry.buildLeagueUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MATCH: {
                long _id = db.insert(MatchContract.MatchEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MatchContract.MatchEntry.buildMatchUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MATCH:
                rowsDeleted = db.delete(
                        MatchContract.MatchEntry.TABLE_NAME, sMatchRoundSelection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //TODO Implement for update data
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MATCH:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MatchContract.MatchEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        return returnCount;
    }
}
