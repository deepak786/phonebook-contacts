package com.phonebook;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * load the contacts using view model architecture component
 */
public class ContactsViewModel extends AndroidViewModel {

    public ContactsViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final Executor executor = Executors.newFixedThreadPool(1);
    public MutableLiveData<ArrayList<Contact>> phonebook = new MutableLiveData<>();

    /**
     * public function to laod the contacts
     */
    public void getContacts() {
        executor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        fetchContacts();
                    }
                }
        );
    }

    /**
     * actual fetching of contacts
     */
    private void fetchContacts() {
        try {
            Cursor cursor = getApplication().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE NOCASE");
            if (cursor != null) {
                ArrayList<Contact> contacts = new ArrayList<>();
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
                cursor.close();
                phonebook.postValue(contacts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get the email of contact using contact id
     */
    private String getEmail(String contactId) {
        String emailStr = "";
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email.ADDRESS};
        final Cursor email = getApplication().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

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
        final Cursor phone = getApplication().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

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
}
