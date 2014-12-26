package com.android.jota.eurofootball;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jota.eurofootball.data.MatchContract;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = " #EurofootballApp";
    private static final int DETAIL_LOADER = 0;

    private String mDateStr;
    private String matchData;

    private TextView local_goals, visitor_goals, local_team, visitor_team, day_match, hour_match, round_match;
    private ImageView local_team_shield, visitor_team_shield;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DetailActivity.DATE_KEY, mDateStr);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDateStr = arguments.getString(DetailActivity.DATE_KEY);
        }
        matchData = FORECAST_SHARE_HASHTAG;

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        local_goals = (TextView) rootView.findViewById(R.id.local_goals_textview);
        local_team = (TextView) rootView.findViewById(R.id.local_team_textview);
        local_team_shield = (ImageView) rootView.findViewById(R.id.local_team_shield);
        visitor_goals = (TextView) rootView.findViewById(R.id.visitor_goals_textview);
        visitor_team = (TextView) rootView.findViewById(R.id.visitor_team_textview);
        visitor_team_shield = (ImageView) rootView.findViewById(R.id.visitor_team_shield);
        day_match = (TextView) rootView.findViewById(R.id.day_match_textview);
        hour_match = (TextView) rootView.findViewById(R.id.hour_match_textview);
        round_match = (TextView) rootView.findViewById(R.id.round_mach_textview);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareForecastIntent());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, matchData + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = "date ASC";
        return new CursorLoader(
                getActivity(),
                MatchContract.MatchEntry.buildMatchInformation(mDateStr),
                null,
                MatchContract.MatchEntry.COLUMN_MATCH_ID + " = ?",
                new String[]{mDateStr},
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            local_team.setText(data.getString(2));
            local_goals.setText(data.getString(4));
            Picasso.with(getActivity()).load(data.getString(6)).into(local_team_shield);
            visitor_team.setText(data.getString(3));
            visitor_goals.setText(data.getString(5));
            Picasso.with(getActivity()).load(data.getString(7)).into(visitor_team_shield);
            day_match.setText(data.getString(9));
            hour_match.setText(data.getString(10) + ":" + data.getString(11));
            round_match.setText("Round " + data.getString(8));

            matchData = String.format("%s %s - %s %s",
                    data.getString(2), data.getString(4), data.getString(5), data.getString(3));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
