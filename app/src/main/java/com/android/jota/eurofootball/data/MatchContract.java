package com.android.jota.eurofootball.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MatchContract {

    public static final String CONTENT_AUTHORITY = "com.android.jota.europeanfootball";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LEAGUE = "league";
    public static final String PATH_MATCH = "match";

    public static final class LeagueEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LEAGUE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LEAGUE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LEAGUE;

        public static final String TABLE_NAME = "league";
        public static final String COLUMN_NAME = "name_league";
        public static final String COLUMN_LEAGUE_ID = "league_id";
        public static final String COLUMN_YEAR = "year";

        public static Uri buildLeagueUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class MatchEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MATCH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MATCH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MATCH;

        public static final String TABLE_NAME = "match";
        public static final String COLUMN_MATCH_ID = "match_id";
        public static final String COLUMN_LOCAL_TEAM = "local_team";
        public static final String COLUMN_VISITOR_TEAM = "visitor_team";
        public static final String COLUMN_LOCAL_GOALS = "local_goals";
        public static final String COLUMN_VISITOR_GOALS = "visitor_goals";
        public static final String COLUMN_LOCAL_SHIELD = "local_shield";
        public static final String COLUMN_VISITOR_SHIELD = "visitor_shield";
        public static final String COLUMN_ROUND = "round";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTE = "minute";
        public static final String COLUMN_LEAGUE_ID = "league_id";

        public static Uri buildMatchUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMatchInformation(String leagueId) {
            return CONTENT_URI.buildUpon().appendPath(leagueId).build();
        }

    }

}
