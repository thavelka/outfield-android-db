package com.outfieldapp.outfieldbackend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.outfieldapp.outfieldbackend.api.Constants;
import com.outfieldapp.outfieldbackend.api.OutfieldAPI;
import com.outfieldapp.outfieldbackend.api.SyncController;
import com.outfieldapp.outfieldbackend.models.Address;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Email;
import com.outfieldapp.outfieldbackend.models.Phone;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //OutfieldApp.getSharedPrefs().edit().clear().commit();
        //OutfieldApp.getDatabase().clear();
        signIn();
    }

    public void signIn() {
        OutfieldAPI.signIn("tim.havelka@gmail.com", "fortune1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    if (user != null) {
                        Log.d(TAG, "Email: " + user.getEmail() + " Token: " + user.getToken());
                        OutfieldApp.getSharedPrefs().edit()
                                .putString(Constants.Headers.EMAIL, user.getEmail())
                                .putString(Constants.Headers.AUTH_TOKEN, user.getToken())
                                .commit();
                        OutfieldAPI.setAuthHeaders(user.getEmail(), user.getToken());
                        runTest();
                    }
                }, throwable -> {});
    }

    public void runTest() {
        // insert some contacts
//        Contact contact = getSampleContact();
//        contact.setName("NEW BACKEND 10");
//        contact.setDirty(true);
//        contact.save();
//
//        Contact contact1 = getSampleContact();
//        contact1.setName("NEW BACKEND 8 EDIT 2");
//        contact1.setDirty(true);
//        contact1.setId(55618);
//        contact1.save();
//
//        Contact contact2 = getSampleContact();
//        contact2.setName("NEW BACKEND 9");
//        contact2.setDirty(true);
//        contact2.setDestroy(true);
//        contact2.setId(55617);
//        contact2.save();

        SyncController.getInstance().doSync();
    }

    public Contact getSampleContact() {
        Contact contact = new Contact();
        contact.setContactType(Contact.Type.PERSON);
        contact.setName("Tim Havelka");
        contact.setTitle("Android Developer");
        contact.setCompany("Outfield");
        contact.setWebsite("http://github.com/thavelka");

        List<Address> addresses = new ArrayList<>();
        Address address = new Address();
        address.setLabel("Apartment");
        address.setStreet1("4504 College Main St.");
        address.setStreet2("#313");
        address.setCity("Bryan");
        address.setRegion("Texas");
        address.setPostalCode("77801");
        address.setCountry("United States");
        addresses.add(address);

        Address address1 = new Address();
        address1.setLabel("Home");
        address1.setStreet1("2115 Seven Maples Dr.");
        address1.setCity("Kingwood");
        address1.setRegion("Texas");
        address1.setPostalCode("77345");
        address1.setCountry("United States");
        addresses.add(address1);

        List<Email> emails = new ArrayList<>();
        Email email = new Email();
        email.setLabel("Personal");
        email.setValue("tim.havelka@gmail.com");
        emails.add(email);

        Email email1 = new Email();
        email1.setLabel("Work");
        email1.setValue("tim@outfieldapp.com");
        emails.add(email1);

        List<Phone> phones = new ArrayList<>();
        Phone phone = new Phone();
        phone.setLabel("Home");
        phone.setValue("2813607335");
        phones.add(phone);

        Phone phone1 = new Phone();
        phone1.setLabel("Cell");
        phone1.setValue("8322174877");
        phones.add(phone1);

        contact.setAddresses(addresses);
        contact.setEmails(emails);
        contact.setPhones(phones);

        return contact;
    }
}
