package com.outfieldapp.outfieldbackend.api;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.api.Constants.Keys;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Interaction;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private Response() {}

    public static class ContactsResponse {
        @SerializedName(Keys.Response.Contacts.CONTACTS_COUNT)
        int contactsCount;
        @SerializedName(Keys.Response.Contacts.PAGES_COUNT)
        int pagesCount;
        @SerializedName(Keys.Response.Contacts.PAGE)
        int page;
        @SerializedName(Keys.Response.Contacts.PER_PAGE)
        int perPage;
        @SerializedName(Keys.Response.Contacts.CONTACTS)
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

    public static class InteractionsResponse {
        @SerializedName(Keys.Response.Interactions.INTERACTIONS_COUNT)
        int interactionsCount;
        @SerializedName(Keys.Response.Interactions.PAGES_COUNT)
        int pagesCount;
        @SerializedName(Keys.Response.Interactions.PAGE)
        int page;
        @SerializedName(Keys.Response.Interactions.PER_PAGE)
        int perPage;
        @SerializedName(Keys.Response.Interactions.INTERACTIONS)
        List<Interaction> interactions = new ArrayList<>();

        public List<Interaction> getInteractions() {
            return interactions;
        }

        public int getInteractionsCount() {
            return interactionsCount;
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

    public static class SyncResponse {
        String status = "";

        @SerializedName(Keys.Response.Sync.SYNC_COUNT)
        int syncCount;
        @SerializedName(Keys.Response.Sync.REMAINING_COUNT)
        int remainingCount;
        @SerializedName(Keys.Response.Sync.CONTACTS_COUNT)
        int contactsCount;
        @SerializedName(Keys.Response.Sync.INTERACTIONS_COUNT)
        int interactionsCount;
        @SerializedName(Keys.Response.Sync.TOKEN)
        String token;
        @SerializedName(Keys.Response.Sync.CONTACTS)
        ContactsSyncResponse contacts;
        @SerializedName(Keys.Response.Sync.INTERACTIONS)
        InteractionsSyncResponse interactions;

        public ContactsSyncResponse getContacts() {
            return contacts;
        }

        public int getContactsCount() {
            return contactsCount;
        }

        public InteractionsSyncResponse getInteractions() {
            return interactions;
        }

        public int getInteractionsCount() {
            return interactionsCount;
        }

        public int getRemainingCount() {
            return remainingCount;
        }

        public String getStatus() {
            return status;
        }

        public int getSyncCount() {
            return syncCount;
        }

        public String getToken() {
            return token;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ContactsSyncResponse {
        @SerializedName(Keys.Response.Sync.CREATE_COUNT)
        int createCount;
        @SerializedName(Keys.Response.Sync.UPDATE_COUNT)
        int updateCount;
        @SerializedName(Keys.Response.Sync.DELETE_COUNT)
        int deleteCount;
        @SerializedName(Keys.Response.Sync.CREATE)
        List<Contact> createdContacts = new ArrayList<>();
        @SerializedName(Keys.Response.Sync.UPDATE)
        List<Contact> updatedContacts = new ArrayList<>();
        @SerializedName(Keys.Response.Sync.DELETE)
        List<Integer> deletedContactIds = new ArrayList<>();

        public int getCreateCount() {
            return createCount;
        }

        public List<Contact> getCreatedContacts() {
            return createdContacts;
        }

        public int getDeleteCount() {
            return deleteCount;
        }

        public List<Integer> getDeletedContactIds() {
            return deletedContactIds;
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public List<Contact> getUpdatedContacts() {
            return updatedContacts;
        }

        public List<Contact> getCurrentContacts() {
            List<Contact> contacts = new ArrayList<>();
            contacts.addAll(createdContacts);
            contacts.addAll(updatedContacts);
            return contacts;
        }
    }

    public static class InteractionsSyncResponse {
        @SerializedName(Keys.Response.Sync.CREATE_COUNT)
        int createCount;
        @SerializedName(Keys.Response.Sync.UPDATE_COUNT)
        int updateCount;
        @SerializedName(Keys.Response.Sync.DELETE_COUNT)
        int deleteCount;
        @SerializedName(Keys.Response.Sync.CREATE)
        List<Interaction> createdInteractions = new ArrayList<>();
        @SerializedName(Keys.Response.Sync.UPDATE)
        List<Interaction> updatedInteractions = new ArrayList<>();
        @SerializedName(Keys.Response.Sync.DELETE)
        List<Integer> deletedInteractionIds = new ArrayList<>();

        public int getCreateCount() {
            return createCount;
        }

        public List<Interaction> getCreatedInteractions() {
            return createdInteractions;
        }

        public int getDeleteCount() {
            return deleteCount;
        }

        public List<Integer> getDeletedInteractionIds() {
            return deletedInteractionIds;
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public List<Interaction> getUpdatedInteractions() {
            return updatedInteractions;
        }

        public List<Interaction> getCurrentInteractions() {
            List<Interaction> interactions = new ArrayList<>();
            interactions.addAll(createdInteractions);
            interactions.addAll(updatedInteractions);
            return interactions;
        }
    }
}
