package com.outfieldapp.outfieldbackend.api;

import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Interaction;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private Response() {}

    public static class ContactsResponse {
        int contactsCount;
        int pagesCount;
        int page;
        int perPage;
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
        int interactionsCount;
        int pagesCount;
        int page;
        int perPage;
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
        int syncCount;
        int remainingCount;
        int contactsCount;
        int interactionsCount;
        String status = "";
        String token;
        ContactsSyncResponse contacts;
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
    }

    public static class ContactsSyncResponse {
        int createCount;
        int updateCount;
        int deleteCount;
        List<Contact> createdContacts = new ArrayList<>();
        List<Contact> updatedContacts = new ArrayList<>();
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
        int createCount;
        int updateCount;
        int deleteCount;
        List<Interaction> createdInteractions = new ArrayList<>();
        List<Interaction> updatedInteractions = new ArrayList<>();
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
