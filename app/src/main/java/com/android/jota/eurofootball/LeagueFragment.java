package com.android.jota.eurofootball;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.jota.eurofootball.data.MatchContract.MatchEntry;
import com.android.jota.eurofootball.sync.EurofootballSyncAdapter;

public class LeagueFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String SELECTED_KEY = "selected_position";
    private static final int MATCH_LOADER = 0;

    private LeagueAdapter mLeagueAdapter;
    private String mMatch;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    public interface Callback {
        public void onItemSelected(String date);
    }

    public LeagueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLeagueAdapter = new LeagueAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mLeagueAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mLeagueAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity()).onItemSelected(cursor.getString(1));
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATCH_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMatch != null && !mMatch.equals(Utility.getPreferredLeague(getActivity()))) {
            getLoaderManager().restartLoader(MATCH_LOADER, null, this);
            ((MainActivity) getActivity()).clearAllViews();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = "date ASC";

        mMatch = Utility.getPreferredLeague(getActivity());
        Uri weatherForLocationUri = MatchEntry.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLeagueAdapter.swapCursor(data);
        if (!data.moveToFirst()) {
            EurofootballSyncAdapter.syncImmediately(getActivity());
        }
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLeagueAdapter.swapCursor(null);
    }

}
