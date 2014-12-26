package com.android.jota.eurofootball;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class LeagueAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_MATCH = 0;

    public static class ViewHolder {
        public final ImageView localImage, visitorImage;
        public final TextView localView, localGoalsView;
        public final TextView visitorView, visitorGoalsView;

        public ViewHolder(View view) {
            localImage = (ImageView) view.findViewById(R.id.local_imageview);
            localView = (TextView) view.findViewById(R.id.local_team_textview);
            localGoalsView = (TextView) view.findViewById(R.id.local_goals_textview);
            visitorImage = (ImageView) view.findViewById(R.id.visitor_imageview);
            visitorView = (TextView) view.findViewById(R.id.visitor_team_textview);
            visitorGoalsView = (TextView) view.findViewById(R.id.visitor_goals_textview);
        }
    }

    public LeagueAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = R.layout.list_item_match;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso.with(context).load(cursor.getString(6)).into(viewHolder.localImage);
        viewHolder.localView.setText(cursor.getString(2));
        viewHolder.localGoalsView.setText(cursor.getString(4));
        Picasso.with(context).load(cursor.getString(7)).into(viewHolder.visitorImage);
        viewHolder.visitorView.setText(cursor.getString(3));
        viewHolder.visitorGoalsView.setText(cursor.getString(5));
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_MATCH;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}