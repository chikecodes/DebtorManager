package com.kolocoda.debtormanager;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kolocoda.debtormanager.db.DebtorManagerContentProvider;
import com.kolocoda.debtormanager.db.DebtorManagerContract;
import com.kolocoda.debtormanager.db.DebtorManagerDataSource;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    public static final String EXTRA_DEBT_STATUS = "com.kolocoda.debtormanager.debt_status";

    private static final int COL_DEBTOR_ID = 0;
    private static final int COL_DEBTOR_NAME = 1;
    private static final int COL_DEBTOR_PHONE_NUMBER = 2;
    private static final int COL_DEBTOR_STATUS = 3;
    private static final int COL_DEBTOR_AMOUNT = 4;
    private static final int COL_DEBTOR_DATE_ENTERED = 5;
    private static final int COL_DEBTOR_DATE_DUE = 6;
    private static final int COL_DEBTOR_NOTE = 7;

    private ImageView mDebtorImageView;
    private TextView mNameTextView, mPhoneNoTextView, mAmountTextView, mDateCreatedTextView, mDateDueTextView, mNoteTextView;
    private Button mPaidButton, mCallButton, mSmsButton;

    DebtorManagerDataSource dataSource;

    boolean delete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new DebtorManagerDataSource(getActivity());
        String title = "You Owe Me";
        if (getActivity().getIntent().getIntExtra(EXTRA_DEBT_STATUS, 0) == 1) {
            title = "I Owe You";
        }
        getActivity().setTitle(title);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(DetailActivity.DEBTOR_ID)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, parent, false);
        final Bundle extras = getActivity().getIntent().getExtras();

        mDebtorImageView = (ImageView) v.findViewById(R.id.debtor_imageView);
        mNameTextView = (TextView) v.findViewById(R.id.name_textView);
        mPhoneNoTextView = (TextView) v.findViewById(R.id.phone_no_textView);
        mAmountTextView = (TextView) v.findViewById(R.id.amount_textView);
        mDateCreatedTextView = (TextView) v.findViewById(R.id.date_created_textView);
        mDateDueTextView = (TextView) v.findViewById(R.id.date_due_textView);
        mNoteTextView = (TextView) v.findViewById(R.id.note_textView);

        mPaidButton = (Button) v.findViewById(R.id.paid_button);
        mPaidButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long debtorId = getArguments().getLong(DetailActivity.DEBTOR_ID);
                Uri uri = Uri.parse(DebtorManagerContentProvider.CONTENT_URI + "/" + debtorId);
                if (dataSource.removeDebtor(uri)) {
                    Toast.makeText(getActivity(), "Successfully removed", Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
            }
        });

        mCallButton = (Button) v.findViewById(R.id.call_button);
        mCallButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + mPhoneNoTextView.getText()));
                startActivity(i);
            }
        });

        mSmsButton = (Button) v.findViewById(R.id.sms_button);
        mSmsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("address", mPhoneNoTextView.getText());
                i.putExtra("sms_body", "MY MONEY GUY!!");
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        long debtorId = getArguments().getLong(DetailActivity.DEBTOR_ID);
        Uri debtorUri = Uri.parse(DebtorManagerContentProvider.CONTENT_URI + "/" + debtorId);
        return new CursorLoader(
                getActivity(),
                debtorUri,
                DebtorManagerContract.DebtsEntry.getAllColumns(),
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }
        mDebtorImageView.setImageDrawable(null);
        String debtorName = data.getString(COL_DEBTOR_NAME);
        mNameTextView.setText(debtorName);
        String debtorPhoneNo = data.getString(COL_DEBTOR_PHONE_NUMBER);
        mPhoneNoTextView.setText(debtorPhoneNo);
        String debtAmount = String.format("%,.2f", data.getDouble(COL_DEBTOR_AMOUNT));
        mAmountTextView.setText(debtAmount);
        String dateEntered = data.getString(COL_DEBTOR_DATE_ENTERED);
        mDateCreatedTextView.setText(dateEntered);
        String dateDue = data.getString(COL_DEBTOR_DATE_DUE);
        mDateDueTextView.setText(dateDue);
        String note = data.getString(COL_DEBTOR_NOTE);
        mNoteTextView.setText(note);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(DetailActivity.DEBTOR_ID)) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_item_edit_debtor:
                long editDebtorId = getArguments().getLong(DetailActivity.DEBTOR_ID);
                Uri editDebtorUri = Uri.parse(DebtorManagerContentProvider.CONTENT_URI + "/" + editDebtorId);
                boolean clickedEditDebtor = true;
                Intent i = new Intent(getActivity(), AddOrEditDebtorActivity.class);
                i.putExtra(DebtorManagerContentProvider.CONTENT_ITEM_TYPE, editDebtorUri);
                i.putExtra(AddOrEditDebtorFragment.EXTRA_EDIT_DEBTOR, clickedEditDebtor);
                startActivityForResult(i, 0);
                return true;
            case R.id.menu_item_delete_debtor:
                long deleteDebtorId = getArguments().getLong(DetailActivity.DEBTOR_ID);
                Uri deleteDebtorUri = Uri.parse(DebtorManagerContentProvider.CONTENT_URI + "/" + deleteDebtorId);
                if (dataSource.removeDebtor(deleteDebtorUri)) {
                    Toast.makeText(getActivity(), "Successfully removed", Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
