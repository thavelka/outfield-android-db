package com.outfieldapp.outfieldbackend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.outfieldapp.outfieldbackend.models.Address;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Email;
import com.outfieldapp.outfieldbackend.models.Phone;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
