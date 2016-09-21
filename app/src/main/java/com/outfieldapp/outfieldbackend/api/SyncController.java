package com.outfieldapp.outfieldbackend.api;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.api.response.SyncResponse;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Image;
import com.outfieldapp.outfieldbackend.models.Interaction;
import com.outfieldapp.outfieldbackend.models.User;

import java.util.ArrayList;
import java.util.List;

public class SyncController {
    private static final String TAG = SyncController.class.getSimpleName();
    private static SyncController instance = new SyncController();
    public static SyncController getInstance() {
        return instance;
    }
    private SyncController() {}

    private boolean isSyncing;
    private int progress;
    private int syncTotal;
    private boolean hasTeamActivity;
    private boolean userInfoCurrent;
    private String syncToken = "";

    private List<Long> pendingContacts = new ArrayList<>();
    private List<Long> pendingInteractions = new ArrayList<>();
    private List<Long> pendingComments = new ArrayList<>();
    private List<Long> pendingImages = new ArrayList<>();


    public void doSync() {
        if (isSyncing) return;
        if (!userInfoCurrent) {
            getUserDetails();
            return;
        }

        Log.d(TAG, "Starting sync");
        // TODO: Send broadcast intent

        SharedPreferences prefs = OutfieldApp.getSharedPrefs();
        String syncToken = prefs.getString(Constants.Headers.SYNC_TOKEN, null);

        isSyncing = true;
        progress = 0;
        syncTotal = 0;
        userInfoCurrent = false;

        syncCurrentUser();
        syncForms();
        syncContacts();
        syncInteractions();
        syncContactImages();
        syncInteractionImages();
        syncComments();
        sync(false, syncToken);
    }

    /**
     * Gets up-to-date info about the current user and ensures that the user's account is active.
     */
    private void getUserDetails() {
        OutfieldApi.getUserDetails(new OutfieldApi.ResponseCallback<User>() {
            @Override
            public void onResponse(boolean success, User object) {
                if (success && object != null) {
                    object.save();
                    hasTeamActivity = object.hasTeamActivity();
                    if (!object.isActive()) return;
                    userInfoCurrent = true;
                    doSync();
                }
            }
        });
    }

    /**
     * Updates user's data on the server.
     */
    private void syncCurrentUser() {
        final User currentUser = User.getCurrentUser();
        if (currentUser == null || !currentUser.isDirty()) return;
        OutfieldApi.updateUser(currentUser, new OutfieldApi.ResponseCallback<User>() {
            @Override
            public void onResponse(boolean success, User object) {
                if (success && object != null) {
                    object.setDirty(false);
                    object.setImage(currentUser.getImage());
                    object.save();
                    Log.d(TAG, "Updated user on server.");
                    syncUserImage();
                }
            }
        });
    }

    /**
     * Updates users image on the server. Should only be called after {@link #syncCurrentUser()}.
     */
    private void syncUserImage() {
        // TODO: syncUserImage
    }

    /**
     * Sends local contact changes to server and updates local contacts with response data.
     */
    private void syncContacts() {
        List<Contact> deletedContacts = new ArrayList<>();
        List<Contact> createdContacts = new ArrayList<>();
        List<Contact> updatedContacts = new ArrayList<>();
        List<Contact> favoredContacts = new ArrayList<>();

        // Get dirty contacts
        SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
        Cursor cursor = db.query(
                OutfieldContract.Contact.TABLE_NAME,
                null,
                OutfieldContract.Contact.DIRTY + "=?",
                new String[]{"1"},
                null, null, null
        );

        // Sort dirty contacts
        while (cursor != null && cursor.moveToNext()) {
            Contact contact = new Contact(cursor);
            long id = contact.getId();
            if (pendingContacts.contains(id)) {
                continue;
            }
            pendingContacts.add(id);
            if (contact.isDirty()) {
                deletedContacts.add(contact);
            } else if (id > 0 && contact.isFavored()) {
                favoredContacts.add(contact);
            } else if (id > 0) {
                updatedContacts.add(contact);
            } else if (id < 0) {
                createdContacts.add(contact);
            }
        }
        if (cursor != null) cursor.close();

        // Sync deleted contacts
        for (final Contact contact : deletedContacts) {
            OutfieldApi.deleteContact(contact.getId(), new OutfieldApi.ResponseCallback<Void>() {
                @Override
                public void onResponse(boolean success, Void object) {
                    pendingContacts.remove(contact.getId());
                    if (success) {
                        contact.delete();
                    }
                }
            });
        }

        // Favor and update contacts
        for (final Contact contact : favoredContacts) {
            if (contact.getContactType() == Contact.Type.PLACE) {
                contact.setImages(new ArrayList<Image>());
            }
            OutfieldApi.updateAndFavorContact(contact, new OutfieldApi.ResponseCallback<Contact>() {
                @Override
                public void onResponse(boolean success, Contact object) {
                    if (success && object != null) {
                        object.setDirty(false);
                        object.save();
                        Log.d(TAG, "Favored and updated contact on server.");
                    }
                    pendingContacts.remove(contact.getId());
                }
            });
        }

        // Sync created contacts
        for (final Contact contact : favoredContacts) {
            final long originalId = contact.getId();
            contact.setId(0);
            OutfieldApi.createContact(contact, new OutfieldApi.ResponseCallback<Contact>() {
                @Override
                public void onResponse(boolean success, Contact object) {
                    if (success && object != null) {
                        contact.setId(object.getId());
                        contact.update();

                        Log.d(TAG, "Created contact on server.");
                        object.setImages(contact.getImages());
                        object.setDirty(false);
                        object.save();

                        // Update contactId for related interactions
//                        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
//                        ContentValues values = new ContentValues();
//                        values.put(OutfieldContract.Interaction.CONTACT_ID, object.getId());
//                        db.update(OutfieldContract.Interaction.TABLE_NAME, values,
//                                OutfieldContract.Interaction.CONTACT_ID + "=?",
//                                new String[]{String.valueOf(originalId)});
                    }
                    pendingContacts.remove(originalId);
                }
            });
        }

        // Sync updated contacts
        for (final Contact contact : updatedContacts) {
            if (contact.getContactType() == Contact.Type.PLACE) {
                contact.setImages(new ArrayList<Image>());
            }
            OutfieldApi.updateContact(contact, new OutfieldApi.ResponseCallback<Contact>() {
                @Override
                public void onResponse(boolean success, Contact object) {
                    if (success && object != null) {
                        Log.d(TAG, "Updated contact on server.");
                        object.setDirty(false);
                        object.save();
                    }
                    pendingContacts.remove(contact.getId());
                }
            });
        }
    }

    private void syncInteractions() {

    }

    private void syncContactImages() {

    }

    private void syncInteractionImages() {

    }

    private void syncComments() {

    }

    private void syncNotifications() {

    }

    private void syncForms() {

    }

    private void sync(final Boolean onlyMe, final String syncToken) {

        final String oldToken = this.syncToken;
        this.syncToken = syncToken;

        OutfieldApi.sync(onlyMe, 50, syncToken, new OutfieldApi.ResponseCallback<SyncResponse>() {
            @Override
            public void onResponse(boolean success, SyncResponse object) {

                // If failed, reset token so user can try again later
                if (!success) {
                    setSyncToken(oldToken);
                    onSyncFinished(false);
                    return;
                }

                // Save new sync token
                String newToken = object.getToken();
                SharedPreferences.Editor editor = OutfieldApp.getSharedPrefs().edit();
                editor.putString(Constants.Headers.SYNC_TOKEN, newToken);
                editor.apply();

                // If not finished, call sync again
                if (object.getStatus().equals("more")) {
                    sync(onlyMe, newToken);
                }

                // Get changes from response
                List<Contact> contacts = new ArrayList<Contact>();
                List<Integer> deletedContacts = new ArrayList<Integer>();
                if (object.getContacts() != null) {
                    contacts.addAll(object.getContacts().getCurrentContacts());
                    deletedContacts.addAll(object.getContacts().getDeletedContactIds());
                }

                List<Interaction> interactions = new ArrayList<Interaction>();
                List<Integer> deletedInteractions = new ArrayList<Integer>();
                if (object.getInteractions() != null) {
                    interactions.addAll(object.getInteractions().getCurrentInteractions());
                    deletedInteractions.addAll(object.getInteractions().getDeletedInteractionIds());
                }

                // Create/update interactions from server
                for (Interaction interaction : interactions) {
                    interaction.setDirty(false);
                    interaction.save();
                }

                // Destroy interactions deleted remotely
                SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
                db.beginTransaction();
                for (Integer id : deletedInteractions) {
                    db.delete(OutfieldContract.Interaction.TABLE_NAME,
                            OutfieldContract.Interaction.INTERACTION_ID + "=?",
                            new String[]{String.valueOf(id)});
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                // Create/update contacts from server
                for (Contact contact : contacts) {
                    contact.setDirty(false);
                    contact.save();
                }

                // Destroy contacts deleted remotely
                db.beginTransaction();
                for (Integer id : deletedContacts) {
                    db.delete(OutfieldContract.Contact.TABLE_NAME,
                            OutfieldContract.Contact.CONTACT_ID + "=?",
                            new String[]{String.valueOf(id)});
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                // TODO: update progress

                // Sync notifications and disable loading screen for future app launches
                if (object.getStatus().equals("done")) {
                    syncNotifications();
                    // TODO: mark showLoadingScreen false
                    onSyncFinished(true);
                }
            }
        });
    }

    private void onSyncFinished(boolean success) {
        isSyncing = false;
        Log.d(TAG, "Sync finished. Success = " + success);
        // TODO: Send broadcast intent
    }

    private void reset() {

    }

    private void setSyncToken(String token) {
        syncToken = token;
    }
}
