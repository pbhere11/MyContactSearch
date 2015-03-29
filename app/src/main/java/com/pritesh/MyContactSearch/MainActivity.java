package com.pritesh.MyContactSearch;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.HashSet;


public class MainActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    private ArrayList<Contacts> cntcts;
    private EditText srchContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srchContact = (EditText) findViewById(R.id.searchName);
        srchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getLoaderManager().destroyLoader(0);
                getLoaderManager().initLoader(0, null, MainActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contactUri = ContactsContract.Data.CONTENT_URI;
        String[] PROJECTION = {
                ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.Contacts.Photo.PHOTO,
                ContactsContract.Data.HAS_PHONE_NUMBER
        };
        String SELECTION =
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME + " like '" + srchContact.getText().toString() + "%'" +
                " OR " +
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME + " like '" + srchContact.getText().toString() + "%'" +
                " OR " +
                ContactsContract.Data.DISPLAY_NAME + " like '" + srchContact.getText().toString() + "%'";
        String contactName = null;
        CursorLoader cursorLoader = new CursorLoader(MainActivity.this);
        cursorLoader.setUri(contactUri);
        cursorLoader.setProjection(PROJECTION);
        cursorLoader.setSelection(SELECTION);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        HashSet<Integer> contactsSet = new HashSet<Integer>();
        data.moveToFirst();
        cntcts = new ArrayList<Contacts>();
        while (data.moveToNext()) {
            if (data.getInt(5) == 1&&!contactsSet.contains(data.getInt(0))) {
                String name = data.getString(1);
                byte[] icon = data.getBlob(4);
                cntcts.add(new Contacts(name, icon));
                contactsSet.add(data.getInt(0));
            }
        }
        populateListView(cntcts);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void populateListView(ArrayList<Contacts> data) {
        ArrayAdapter<Contacts> arrAdapter = new MyListAdapter(data);
        ListView contactListView = (ListView) findViewById(R.id.contactsList);
        contactListView.setAdapter(arrAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyListAdapter extends ArrayAdapter<Contacts> {
        ArrayList<Contacts> data;

        public MyListAdapter(ArrayList<Contacts> data) {
            super(MainActivity.this, R.layout.contact_view, data);
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.contact_view, parent, false);
            }
            Contacts contact = data.get(position);
            ImageView imgView = (ImageView) itemView.findViewById(R.id.contactImage);
            if (contact.getIcon() != null) {
                imgView.setImageBitmap(BitmapFactory.decodeByteArray(contact.getIcon(), 0, contact.getIcon().length));
            }
            TextView txtView = (TextView) itemView.findViewById(R.id.contactName);
            txtView.setText(contact.getName());
            return itemView;
        }
    }


}
