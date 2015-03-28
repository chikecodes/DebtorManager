package com.kolocoda.debtormanager;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class DetailActivity extends ActionBarActivity {

    public static String DEBTOR_ID = "com.kolocoda.debtmanager.debtor_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            long id = getIntent().getLongExtra(DEBTOR_ID, -1);

            Bundle arguments = new Bundle();
            arguments.putLong(DEBTOR_ID, id);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }



}
