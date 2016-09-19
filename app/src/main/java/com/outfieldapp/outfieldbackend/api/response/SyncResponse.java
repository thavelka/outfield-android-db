package com.outfieldapp.outfieldbackend.api.response;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.api.Constants;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Interaction;

import java.util.ArrayList;
import java.util.List;

public class SyncResponse {
    String status = "";

    @SerializedName(Constants.Keys.Response.Sync.SYNC_COUNT)
    int syncCount;
    @SerializedName(Constants.Keys.Response.Sync.REMAINING_COUNT)
    int remainingCount;
    @SerializedName(Constants.Keys.Response.Sync.CONTACTS_COUNT)
    int contactsCount;
    @SerializedName(Constants.Keys.Response.Sync.INTERACTIONS_COUNT)
    int interactionsCount;
    @SerializedName(Constants.Keys.Response.Sync.TOKEN)
    String token;
    @SerializedName(Constants.Keys.Response.Sync.CONTACTS)
    ContactsSyncResponse contacts;
    @SerializedName(Constants.Keys.Response.Sync.INTERACTIONS)
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

    public static class ContactsSyncResponse {
        @SerializedName(Constants.Keys.Response.Sync.CREATE_COUNT)
        int createCount;
        @SerializedName(Constants.Keys.Response.Sync.UPDATE_COUNT)
        int updateCount;
        @SerializedName(Constants.Keys.Response.Sync.DELETE_COUNT)
        int deleteCount;
        @SerializedName(Constants.Keys.Response.Sync.CREATE)
        List<Contact> createdContacts = new ArrayList<>();
        @SerializedName(Constants.Keys.Response.Sync.UPDATE)
        List<Contact> updatedContacts = new ArrayList<>();
        @SerializedName(Constants.Keys.Response.Sync.DELETE)
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
        @SerializedName(Constants.Keys.Response.Sync.CREATE_COUNT)
        int createCount;
        @SerializedName(Constants.Keys.Response.Sync.UPDATE_COUNT)
        int updateCount;
        @SerializedName(Constants.Keys.Response.Sync.DELETE_COUNT)
        int deleteCount;
        @SerializedName(Constants.Keys.Response.Sync.CREATE)
        List<Interaction> createdInteractions = new ArrayList<>();
        @SerializedName(Constants.Keys.Response.Sync.UPDATE)
        List<Interaction> updatedInteractions = new ArrayList<>();
        @SerializedName(Constants.Keys.Response.Sync.DELETE)
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
