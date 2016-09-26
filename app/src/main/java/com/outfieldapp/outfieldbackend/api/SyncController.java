package com.outfieldapp.outfieldbackend.api;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Form;
import com.outfieldapp.outfieldbackend.models.Interaction;
import com.outfieldapp.outfieldbackend.models.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

// TODO: Implement Counter class or RxJava

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

        syncContacts().subscribe(success -> sync(false, syncToken));

//        syncCurrentUser();
//        syncForms();
//        syncContacts();
//        syncInteractions();
//        syncContactImages();
//        syncInteractionImages();
//        syncComments();
//        sync(false, syncToken);
    }

    /**
     * Gets up-to-date info about the current user and ensures that the user's account is active.
     */
    private void getUserDetails() {
        OutfieldAPI.getUserDetails((success, object) -> {
            if (success && object != null) {
                object.save();
                hasTeamActivity = object.hasTeamActivity();
                if (!object.isActive()) return;
                userInfoCurrent = true;
                doSync();
            }
        });
    }

    /**
     * Updates user's data on the server.
     */
    private void syncCurrentUser() {
        final User currentUser = User.getCurrentUser();
        if (currentUser == null || !currentUser.isDirty()) return;
        OutfieldAPI.updateUser(currentUser, (success, object) -> {
            if (success && object != null) {
                object.setDirty(false);
                object.setImage(currentUser.getImage());
                object.save();
                Log.d(TAG, "Updated user on server.");
                syncUserImage();
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
    private Single<Boolean> syncContacts() {
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
            if (contact.isDestroy()) {
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
        Observable<Contact> delete = Observable.from(deletedContacts)
                .flatMap(contact -> {
                    if (contact.getId() > 0) {
                        return OutfieldAPI.deleteContact(contact);
                    } else {
                        return Observable.just(contact);
                    }
                })
                .doOnNext(contact -> {
                    if (contact != null) contact.delete();
                });

        // Favor and update contacts
        Observable<Contact> favor = Observable.from(favoredContacts)
                .flatMap(contact -> {
                    if (contact.getContactType() == Contact.Type.PLACE) {
                        contact.setImages(new ArrayList<>());
                    }
                    return OutfieldAPI.updateAndFavorContact(contact);
                })
                .doOnNext(returnedContact -> {
                    returnedContact.setDirty(false);
                    returnedContact.save();
                });

        // Sync created contacts
        Observable<Contact> create = Observable.from(createdContacts)
                .flatMap(contact -> {
                    contact.setId(0);
                    return OutfieldAPI.createContact(contact)
                            .doOnNext(returnedContact -> {
                                contact.setId(returnedContact.getId());
                                contact.update();
                                returnedContact.setImages(contact.getImages());
                                returnedContact.setDirty(false);
                                returnedContact.save();
                            });
                });

        // Sync updated contacts
        Observable<Contact> update = Observable.from(updatedContacts)
                .flatMap(contact -> {
                    if (contact.getContactType() == Contact.Type.PLACE) {
                        contact.setImages(new ArrayList<>());
                    }
                    return OutfieldAPI.updateContact(contact);
                })
                .doOnNext(returnedContact -> {
                    returnedContact.setDirty(false);
                    returnedContact.save();
                });

        // Merge observables into single and return
        return Single.create(singleSubscriber ->
                Observable.merge(delete, favor, create, update)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnCompleted(() -> singleSubscriber.onSuccess(true))
                .subscribe());
    }

    /**
     * Sends local interaction changes to server and updates local interactions with response data.
     */
    private void syncInteractions() {
        List<Interaction> deletedInteractions = new ArrayList<>();
        List<Interaction> createdInteractions = new ArrayList<>();
        List<Interaction> updatedInteractions = new ArrayList<>();

        // Get dirty interactions
        SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
        Cursor cursor = db.query(
                OutfieldContract.Interaction.TABLE_NAME,
                null,
                OutfieldContract.Interaction.DIRTY + "=?",
                new String[]{"1"},
                null, null, null
        );

        while (cursor != null && cursor.moveToNext()) {
            Interaction interaction = new Interaction(cursor);
            long id = interaction.getId();

            // Sort dirty interactions
            if (interaction.isDestroy()) {
                deletedInteractions.add(interaction);
            } else if (interaction.getId() > 0) {
                updatedInteractions.add(interaction);
            } else if (interaction.getId() <= 0) {
                createdInteractions.add(interaction);
            }
        }

        if (cursor != null) cursor.close();

        // Sync deleted interactions
        for (final Interaction interaction : deletedInteractions) {
            if (interaction.getId() <= 0) {
                interaction.delete();
            } else {
                OutfieldAPI.deleteInteraction(interaction.getId(), (success, object) -> {
                    if (success) {
                        Log.d(TAG, "Deleted interaction on server.");
                        interaction.delete();
                    }
                });
            }
        }

        // Sync created interactions
        for (final Interaction interaction : createdInteractions) {
            final long originalId = interaction.getId();
            interaction.setId(0);
            OutfieldAPI.createInteraction(interaction, (success, object) -> {
                if (success && object != null) {
                    Log.d(TAG, "Created interaction on server.");
                    interaction.setId(object.getId());
                    interaction.update();

                    object.setImages(interaction.getImages());
                    object.setComments(interaction.getComments());
                    object.setDirty(false);
                    object.save();
                }
            });
        }

        // Sync updated interactions
        for (final Interaction interaction : updatedInteractions) {
            OutfieldAPI.updateInteraction(interaction, (success, object) -> {
                if (success && object != null) {
                    Log.d(TAG, "Updated interaction on server.");
                    object.setImages(interaction.getImages());
                    object.setComments(interaction.getComments());
                    object.setDirty(false);
                    object.save();
                }
            });
        }
    }

    private void syncContactImages() {
        Log.d(TAG, "SYNC CONTACT IMAGES");
    }

    private void syncInteractionImages() {

    }

    private void syncComments() {

    }

    private void syncNotifications() {

    }

    /**
     * Retrieves and inserts organization's current interaction forms.
     */
    private void syncForms() {
        OutfieldAPI.getLatestForms((success, object) -> {
            if (success && object != null) {
                // TODO: Save current form ids to SharedPreferences
                for (Form form : object) form.save();
            }
        });
    }

    /**
     * Gets contact and interaction changes for favored contacts from server.
     * @param onlyMe If false, retrieves interactions by all team members for favored contact.
     * @param syncToken When to begin syncing from. If null, will sync from beginning of time.
     */
    private void sync(final Boolean onlyMe, final String syncToken) {

        final String oldToken = this.syncToken;
        this.syncToken = syncToken;
        OutfieldAPI.sync(onlyMe, 50, syncToken)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(syncResponse -> {
                    // Save new sync token
                    String newToken = syncResponse.getToken();
                    SharedPreferences.Editor editor = OutfieldApp.getSharedPrefs().edit();
                    editor.putString(Constants.Headers.SYNC_TOKEN, newToken);
                    editor.apply();

                    // If not finished, call sync again
                    if (syncResponse.getStatus().equals("more")) {
                        sync(onlyMe, newToken);
                    }

                    // Get changes from response
                    List<Contact> contacts = new ArrayList<Contact>();
                    List<Integer> deletedContacts = new ArrayList<Integer>();
                    if (syncResponse.getContacts() != null) {
                        contacts.addAll(syncResponse.getContacts().getCurrentContacts());
                        deletedContacts.addAll(syncResponse.getContacts().getDeletedContactIds());
                    }

                    List<Interaction> interactions = new ArrayList<Interaction>();
                    List<Integer> deletedInteractions = new ArrayList<Integer>();
                    if (syncResponse.getInteractions() != null) {
                        interactions.addAll(syncResponse.getInteractions().getCurrentInteractions());
                        deletedInteractions.addAll(syncResponse.getInteractions().getDeletedInteractionIds());
                    }

                    // TODO: Bulk insert contacts and interactions.
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
                    if (syncResponse.getStatus().equals("done")) {
                        syncNotifications();
                        // TODO: mark showLoadingScreen false
                        onSyncFinished(true);
                    }
                }, throwable -> {
                    setSyncToken(oldToken);
                    onSyncFinished(false);
                });
    }

    /**
     * To be called when sync finishes or fails.
     * @param success True if sync completed successfully.
     */
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
