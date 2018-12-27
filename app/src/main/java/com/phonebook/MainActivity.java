package com.phonebook;

import android.Manifest;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import com.phonebook.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, EasyPermissions.PermissionCallbacks {
    private ActivityMainBinding binding;
    private List<Contact> contacts;
    private final String permission = Manifest.permission.READ_CONTACTS;
    private static final int PERMISSION_CODE = 958;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // initialize th array list
        contacts = new ArrayList<>();
        // set the layout manager
        binding.list.setLayoutManager(new LinearLayoutManager(this));

        // check for permission READ_CONTACTS
        if (EasyPermissions.hasPermissions(this, permission)) {
            // Already have permission, do the thing
            loadContacts();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Please allow the permission to use the functinality",
                    PERMISSION_CODE, permission);
        }

    }

    /**
     * initialize the loader to load the contacts
     */
    private void loadContacts() {
        // initialize the loader
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE NOCASE"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                if ("1".equals(hasPhone) || Boolean.parseBoolean(hasPhone)) {
                    // contact has phone number
                    ArrayList<String> phones = getPhoneNumber(contactId);
                    if (phones.size() > 0) {
                        Contact contact = new Contact();
                        contact.setName(name);
                        contact.setEmail(getEmail(contactId));
                        contact.setPhones(phones);
                        // add this contact to the array list
                        contacts.add(contact);
                    }
                }
            }
            // set the adapter
            binding.list.setAdapter(new ContactsAdapter(contacts));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // loader is re-initialized so reset the array list
        contacts = new ArrayList<>();
    }

    /**
     * get the email of contact using contact id
     */
    private String getEmail(String contactId) {
        String emailStr = "";
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email.ADDRESS};
        final Cursor email = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

        if (email != null) {
            if (email.getCount() > 0) {
                email.moveToFirst();
                final int contactEmailColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                emailStr = email.getString(contactEmailColumnIndex);
            }
            email.close();
        }
        return emailStr;

    }

    /**
     * get the list of phone numbers associated with contact
     */
    private ArrayList<String> getPhoneNumber(String contactId) {
        ArrayList<String> phones = new ArrayList<>();
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.Data.RAW_CONTACT_ID};
        final Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

        if (phone != null) {
            while (phone.moveToNext()) {
                String raw_id = phone.getString(phone.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));

                int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                String phoneNo = phone.getString(contactNumberColumnIndex);

                if (phones.size() == 0) {
                    // this is the first phone number
                    phones.add(phoneNo);
                } else {
                    // another phone numbers of contact
                    // so compare if that numaber is already added or not
                    boolean alreadyAdded = false;
                    for (String number : phones) {
                        // the below comparision returns true if two numbers are identical enough for caller ID purposes.
                        if (PhoneNumberUtils.compare(phoneNo, number)) {
                            // number is matched with any of already added numbers
                            alreadyAdded = true;
                            break;
                        }
                    }
                    if (!alreadyAdded)
                        phones.add(phoneNo);
                }
            }
            phone.close();
        }
        return phones;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> list) {
        if (requestCode == PERMISSION_CODE) {
            // permission granted
            loadContacts();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> list) {
        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
    }
}
