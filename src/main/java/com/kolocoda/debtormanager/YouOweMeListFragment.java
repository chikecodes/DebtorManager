package com.kolocoda.debtormanager;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.kolocoda.debtormanager.db.DebtorManagerContentProvider;
import com.kolocoda.debtormanager.db.DebtorManagerContract;
import com.kolocoda.debtormanager.db.DebtorManagerDataSource;


public class YouOweMeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int YOU_OWE_LOADER = 0;
    private static final String SELECTED_KEY = "com.kolocoda.debtormanager.youowe_selected_key";
    DebtorAdapter mAdapter;
    DebtorManagerDataSource mDataSource;
    private int mPosition;
    ListView mListView;

    public interface Callback {
        public void onItemSelected(long id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.you_owe_me_title);
        mDataSource = new DebtorManagerDataSource(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(YOU_OWE_LOADER, null, this);
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        Cursor cur = getActivity().getContentResolver().query(DebtorManagerContentProvider.CONTENT_URI, DebtorManagerContract.DebtsEntry.getAllColumns(), DebtorManagerContract.DebtsEntry.COLUMN_STATUS + "=" + 0, null, null);
        mAdapter = new DebtorAdapter(getActivity(), cur, 0);

        ListView listView = (ListView)v.findViewById(android.R.id.list);
        ViewParent view = listView.getParent();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        Button addButton = new Button(getActivity());
        addButton.setText("No Debtor(s)!\n\n Add One");
        addButton.setBackgroundResource(R.drawable.button_oval_shape);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                310,
                310
        );
        params.setMargins(0, 32, 0, 0);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addButton.setLayoutParams(params);

        ((ViewGroup) view).addView(addButton);
        listView.setEmptyView(addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean clickedEditDebtor = false;
                Intent i = new Intent(getActivity(), AddOrEditDebtorActivity.class);
                i.putExtra(AddOrEditDebtorFragment.EXTRA_EDIT_DEBTOR, clickedEditDebtor);
                startActivity(i);
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = DebtorManagerContract.DebtsEntry.getAllColumns();
        CursorLoader cursorLoader = new CursorLoader(getActivity(), DebtorManagerContentProvider.CONTENT_URI, projection, DebtorManagerContract.DebtsEntry.COLUMN_STATUS + "=" + 0, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        if((cursor != null && cursor.moveToPosition(position))) {
            ((Callback)getActivity()).onItemSelected(cursor.getLong(0));
        }
        mPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
