package com.kolocoda.debtormanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class IOweYouListActivity extends ActionBarActivity implements IOweYouListFragment.Callback {

    static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ioweyou);
        setTotalView();
        if(findViewById(R.id.detail_container) != null) {
            mTwoPane = true;

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    public static boolean getTwoPane() {
        return mTwoPane;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_new_debtor:
                boolean clickedEditDebtor = false;
                Intent i = new Intent(this, AddOrEditDebtorActivity.class);
                i.putExtra(AddOrEditDebtorFragment.EXTRA_EDIT_DEBTOR, clickedEditDebtor);
                startActivityForResult(i, 0);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(long id) {
        if(mTwoPane) {

            Bundle args = new Bundle();
            args.putLong(DetailActivity.DEBTOR_ID, id);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailActivity.DEBTOR_ID, id);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTotalView();
    }

    public void setTotalView() {
        TextView total = (TextView)findViewById(R.id.footer_title_id);
        String totalFormat = getString(R.string.total, String.format("$" + "%,.2f", Utility.getIOweTotal(this)));
        total.setText(totalFormat);
    }

}
