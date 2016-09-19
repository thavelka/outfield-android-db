package com.outfieldapp.outfieldbackend.api.response;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.api.Constants;
import com.outfieldapp.outfieldbackend.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsResponse {
    @SerializedName(Constants.Keys.Response.Contacts.CONTACTS_COUNT)
    int contactsCount;
    @SerializedName(Constants.Keys.Response.Contacts.PAGES_COUNT)
    int pagesCount;
    @SerializedName(Constants.Keys.Response.Contacts.PAGE)
    int page;
    @SerializedName(Constants.Keys.Response.Contacts.PER_PAGE)
    int perPage;
    @SerializedName(Constants.Keys.Response.Contacts.CONTACTS)
    List<Contact> contacts = new ArrayList<>();

    public List<Contact> getContacts() {
        return contacts;
    }

    public int getContactsCount() {
        return contactsCount;
    }

    public int getPage() {
        return page;
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public int getPerPage() {
        return perPage;
    }
}
