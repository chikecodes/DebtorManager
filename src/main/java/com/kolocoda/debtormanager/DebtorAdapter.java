package com.kolocoda.debtormanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kolocoda.debtormanager.db.DebtorManagerContract;

/**
 * Created by koloCoda on 25/03/2015.
 */
public class DebtorAdapter extends CursorAdapter {

    public DebtorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        if(IOweYouListActivity.getTwoPane() | YouOweMeListActivity.getTwoPane()) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_debtor_tablet, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_debtor, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder)view.getTag();
        String name = cursor.getString(cursor.getColumnIndex(DebtorManagerContract.DebtsEntry.COLUMN_NAME));
        viewHolder.nameView.setText(name);
        //String amount = cursor.getString(cursor.getColumnIndex(DebtorManagerContract.DebtsEntry.COLUMN_AMOUNT));
        viewHolder.amtView.setText(String.format("$" + "%,.2f", cursor.getDouble(cursor.getColumnIndex(DebtorManagerContract.DebtsEntry.COLUMN_AMOUNT))));

        String dateDue = cursor.getString(cursor.getColumnIndex(DebtorManagerContract.DebtsEntry.COLUMN_DATE_DUE));
        viewHolder.dateDueView.setText(dateDue);
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;
        public final TextView amtView;
        public final TextView dateDueView;

        public ViewHolder(View view) {
            imageView = (ImageView)view.findViewById(R.id.debtor_list_item_imageTextView);
            nameView = (TextView)view.findViewById(R.id.debtor_list_item_nameTextView);
            amtView = (TextView)view.findViewById(R.id.debtor_list_item_amountTextView);
            dateDueView = (TextView)view.findViewById(R.id.debtor_list_item_dueDateTextView);
        }
    }


}
