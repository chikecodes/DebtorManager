package com.kolocoda.debtormanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kolocoda.debtormanager.db.DebtorManagerContentProvider;
import com.kolocoda.debtormanager.db.DebtorManagerContract.DebtsEntry;
import com.kolocoda.debtormanager.db.DebtorManagerDataSource;

import java.util.Date;


public class AddOrEditDebtorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String DIALOG_DATE = "date";
    public static final String EXTRA_EDIT_DEBTOR = "mEditDebtor";
    private static final int REQUEST_CONTACT = 1;
    private static final int ADDEDIT_LOADER = 0;

    private static final int REQUEST_DATE = 0;
    boolean mEditDebtor;

    private static final int COL_DEBTOR_ID = 0;
    private static final int COL_DEBTOR_NAME = 1;
    private static final int COL_DEBTOR_PHONE_NUMBER = 2;
    private static final int COL_DEBTOR_STATUS = 3;
    private static final int COL_DEBTOR_AMOUNT = 4;
    private static final int COL_DEBTOR_DATE_ENTERED = 5;
    private static final int COL_DEBTOR_DATE_DUE = 6;
    private static final int COL_DEBTOR_NOTE = 7;

    private EditText mNameEditText;
    private ImageButton mContactAddImageButton;
    private EditText mPhoneNumberEditText;
    private RadioGroup mRadioGroup;
    private EditText mAmountEditText;
    private Button mDateToBePaidButton;
    private EditText mNoteEditText;
    private Button mSaveButton;
    private Button mCancelButton;

    private int mTrue;

    private String mDebtorName;
    private String mPhoneNumber;
    private int mIOwe;
    private String mAmount;
    private Date mDateToBePaid;
    private Date mDateEntered;
    private String mNote;
    private boolean ifDateWasSelected;
    private boolean clickedSaveButton;
    private int dateCompared;

    private long debtorId;
    DebtorManagerDataSource dataSource;
    String dateDue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String title = "Add Debt Details";
        mEditDebtor = getActivity().getIntent().getBooleanExtra(EXTRA_EDIT_DEBTOR, false);
        if(mEditDebtor) {
            title = "Edit Debt Details";
        }
        getActivity().setTitle(title);
        ifDateWasSelected = false;
        clickedSaveButton = false;
        dataSource = new DebtorManagerDataSource(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity().getIntent().getBooleanExtra(EXTRA_EDIT_DEBTOR, false)) {
            getLoaderManager().initLoader(ADDEDIT_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_or_edit_debtor, container, false);

        mNameEditText = (EditText)v.findViewById(R.id.debtor_name);
        mNameEditText.addTextChangedListener( new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                mDebtorName = c.toString();
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
                mDebtorName = c.toString();
            }
        });

        mContactAddImageButton = (ImageButton)v.findViewById(R.id.phone_contact_button);
        mContactAddImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // implicit intent to call contact db and set the name and number automatically
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, REQUEST_CONTACT);
            }
        });

        mPhoneNumberEditText = (EditText)v.findViewById(R.id.debtor_phoneNumber);
        mPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                if(mPhoneNumberEditText.getText().length() > 0 ) {

                    mPhoneNumber = c.toString();
                }

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        mRadioGroup = (RadioGroup)v.findViewById(R.id.myRadioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.i_owe_radioButton:
                        //Toast.makeText(getActivity(), "i owe was checked ", Toast.LENGTH_SHORT).show();
                        mIOwe = 1;
                        break;
                    case R.id.you_owe_radioButton:
                        // Toast.makeText(getActivity(), "u owe was checked ", Toast.LENGTH_SHORT).show();
                        mIOwe = 0;
                        break;
                }

            }
        });

        mAmountEditText = (EditText)v.findViewById(R.id.debt_amount);
        mAmountEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                if(mAmountEditText.getText().length() > 0 ) {
                    mAmount = c.toString();
                }
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }
            public void afterTextChanged(Editable c) {
                mAmount = c.toString();
            }
        });

        mDateToBePaidButton = (Button)v.findViewById(R.id.date_to_be_paid);

        mDateToBePaidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                if(mEditDebtor) {
                    date = Utility.convertStringToDate(dateDue);
                } else {
                    date = new Date();
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(date);
                dialog.setTargetFragment(AddOrEditDebtorFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mNoteEditText = (EditText)v.findViewById(R.id.note_editText);
        mNoteEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                mNote = c.toString();
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        mSaveButton = (Button)v.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateCompared = 0;
                mDateEntered = new Date();
                if(!mEditDebtor) {
                    // not from debtor fragment to edit
                    if( mDateToBePaid != null) {
                        dateCompared = mDateToBePaid.compareTo(mDateEntered);
                    }
                } else {
                    // from the debtor fragment to edit
                    ifDateWasSelected = true;
                    if(mDateToBePaid != null) {
                        dateCompared = 2;
                    }
                }

                if(ifFieldsHasErrors(dateCompared)) {
                    return;
                } else {
                    saveDebtor();
                }
            }
        });

        mCancelButton = (Button)v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goHome();
            }
        });

       // Log.i("kolo", " " + mDebtorName + " " + mAmount + " " + mPhoneNumber + " " + mIOwe + " " + Utility.convertDateToString(mDateToBePaid) + " " + Utility.convertDateToString(mDateEntered) + " " + mNote);

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK ) return ;
        if(requestCode == REQUEST_DATE) {
            mDateToBePaid = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            ifDateWasSelected = true;
            mDateToBePaidButton.setText(Utility.convertDateToString(mDateToBePaid));
        }else if(requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if(c.getCount() == 0 ) {
                c.close();
                return;
            }
            c.moveToFirst();
            mNameEditText.setText(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            mPhoneNumberEditText.setText(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            c.close();
        }
    }

    public void goHome() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
    }

    public void saveDebtor() {

        Log.i("kolo", " " + mDebtorName + " " + mAmount + " " + mPhoneNumber + " " + mIOwe + " " + Utility.convertDateToString(mDateToBePaid) + " " + Utility.convertDateToString(mDateEntered) + " " + mNote);

        ContentValues values = new ContentValues();
        values.put(DebtsEntry.COLUMN_NAME, mDebtorName);
        values.put(DebtsEntry.COLUMN_PHONE_NO, mPhoneNumber);
        values.put(DebtsEntry.COLUMN_STATUS, mIOwe);
        values.put(DebtsEntry.COLUMN_AMOUNT, mAmount);
        values.put(DebtsEntry.COLUMN_DATE_DUE, Utility.convertDateToString(mDateToBePaid));
        values.put(DebtsEntry.COLUMN_DATE_ENTERED, Utility.convertDateToString(mDateEntered));
        values.put(DebtsEntry.COLUMN_NOTE, mNote);

        if(mEditDebtor) {
            if(dataSource.updateDebtor(values, debtorId)){
                Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        }else {
            Uri uri = dataSource.insert(values);
            Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_LONG).show();
        }
        goHome();
    }

    public boolean ifFieldsHasErrors(int dateCompared ) {
        boolean hasError;
        if(mNameEditText.getText().length() == 0 | mAmountEditText.getText().length() == 0 | dateCompared < 0 | !ifDateWasSelected) {
            Log.i("kolo", " " + mNameEditText.getText().length() + " " + mAmountEditText.getText().length() + " " + dateCompared + " " + !ifDateWasSelected);
            hasError = true;
            Toast.makeText(getActivity(), "Not saved, check your fields", Toast.LENGTH_LONG).show();
            return hasError;
        } else {
            hasError = false;
            return hasError;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Bundle extras = getActivity().getIntent().getExtras();
        Uri uri;
        if(extras != null) {
            uri = extras.getParcelable(DebtorManagerContentProvider.CONTENT_ITEM_TYPE);
            debtorId = Long.parseLong(uri.getLastPathSegment());
        } else {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                uri,
                DebtsEntry.getAllColumns(),
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(!data.moveToFirst()) { return; }

        String debtorName = data.getString(COL_DEBTOR_NAME);
        mNameEditText.setText(debtorName);
        String debtorPhoneNo = data.getString(COL_DEBTOR_PHONE_NUMBER);
        mPhoneNumberEditText.setText(debtorPhoneNo);
        mAmountEditText.setText(String.format("%,.2f", data.getDouble(COL_DEBTOR_AMOUNT)));
        int oweStatus = data.getInt(COL_DEBTOR_STATUS);
        if(oweStatus == 1) {
            mRadioGroup.check(R.id.i_owe_radioButton);
            mTrue = 1;
        } else {
            mRadioGroup.check(R.id.you_owe_radioButton);
            mTrue = 0;
        }
        dateDue = data.getString(COL_DEBTOR_DATE_ENTERED);
        mDateToBePaid = Utility.convertStringToDate(dateDue);
        mDateToBePaidButton.setText(dateDue);
        String note = data.getString(COL_DEBTOR_NOTE);
        mNoteEditText.setText(note);
        mSaveButton.setText("Update");


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
