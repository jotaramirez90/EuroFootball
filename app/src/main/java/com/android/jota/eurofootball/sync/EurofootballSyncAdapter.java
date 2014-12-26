package com.android.jota.eurofootball.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.jota.eurofootball.R;
import com.android.jota.eurofootball.Utility;
import com.android.jota.eurofootball.data.MatchContract.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class EurofootballSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = EurofootballSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public EurofootballSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String leagueId = Utility.getPreferredLeague(getContext());

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;
        String format = "json";
        String request = "matchs";
        String key = "071519f3061c719abcb85095a788527d";

        try {
            final String MATCH_BASE_URL =
                    "http://www.resultados-futbol.com/scripts/api/api.php?tz=Europe/Madrid";

            final String REQUEST_PARAM = "req";
            final String FORMAT_PARAM = "format";
            final String KEY_PARAM = "key";
            final String LEAGUE_PARAM = "league";

            Uri builtUri = Uri.parse(MATCH_BASE_URL).buildUpon()
                    .appendQueryParameter(REQUEST_PARAM, request)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(KEY_PARAM, key)
                    .appendQueryParameter(LEAGUE_PARAM, leagueId)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            forecastJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        JSONObject forecastJson = null;
        try {
            forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray("match");

            JSONObject league = weatherArray.getJSONObject(0);
            addLeague(league.getString("league_id"), league.getString("competition_name"), league.getInt("year"));

            Vector<ContentValues> cVVector = new Vector<>(weatherArray.length());

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject matchJson = weatherArray.getJSONObject(i);

                ContentValues matchValues = new ContentValues();
                matchValues.put(MatchEntry.COLUMN_MATCH_ID, matchJson.getInt("id"));
                matchValues.put(MatchEntry.COLUMN_LOCAL_TEAM, matchJson.getString("local"));
                matchValues.put(MatchEntry.COLUMN_VISITOR_TEAM, matchJson.getString("visitor"));
                matchValues.put(MatchEntry.COLUMN_LOCAL_GOALS, matchJson.getString("local_goals"));
                matchValues.put(MatchEntry.COLUMN_VISITOR_GOALS, matchJson.getString("visitor_goals"));
                matchValues.put(MatchEntry.COLUMN_LOCAL_SHIELD, matchJson.getString("local_shield"));
                matchValues.put(MatchEntry.COLUMN_VISITOR_SHIELD, matchJson.getString("visitor_shield"));
                matchValues.put(MatchEntry.COLUMN_ROUND, matchJson.getInt("round"));
                matchValues.put(MatchEntry.COLUMN_DATE, matchJson.getString("date"));
                matchValues.put(MatchEntry.COLUMN_HOUR, matchJson.getInt("hour"));
                matchValues.put(MatchEntry.COLUMN_MINUTE, matchJson.getInt("minute"));
                matchValues.put(MatchEntry.COLUMN_LEAGUE_ID, leagueId);

                cVVector.add(matchValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int round = cvArray[0].getAsInteger(MatchEntry.COLUMN_ROUND) - 1;
                getContext().getContentResolver().delete(MatchEntry.CONTENT_URI, null, new String[]{Integer.toString(round)});
                getContext().getContentResolver().bulkInsert(MatchEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return;
    }

    private long addLeague(String leagueSetting, String leagueName, int yearLeague) {
        long leagueId;

        Cursor leagueCursor = getContext().getContentResolver().query(
                LeagueEntry.CONTENT_URI,
                new String[]{LeagueEntry._ID},
                LeagueEntry.COLUMN_LEAGUE_ID + " = ?",
                new String[]{leagueSetting},
                null
        );

        if (leagueCursor.moveToFirst()) {
            int leagueIdIndex = leagueCursor.getColumnIndex(LeagueEntry._ID);
            leagueId = leagueCursor.getLong(leagueIdIndex);
        } else {
            ContentValues leagueValues = new ContentValues();
            leagueValues.put(LeagueEntry.COLUMN_NAME, leagueName);
            leagueValues.put(LeagueEntry.COLUMN_LEAGUE_ID, leagueSetting);
            leagueValues.put(LeagueEntry.COLUMN_YEAR, yearLeague);

            Uri insertedUri = getContext().getContentResolver().insert(
                    LeagueEntry.CONTENT_URI,
                    leagueValues
            );
            leagueId = ContentUris.parseId(insertedUri);
        }

        return leagueId;
    }


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        EurofootballSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}
