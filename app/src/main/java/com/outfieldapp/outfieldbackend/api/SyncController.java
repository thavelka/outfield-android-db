package com.outfieldapp.outfieldbackend.api;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;
import com.outfieldapp.outfieldbackend.models.Comment;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Form;
import com.outfieldapp.outfieldbackend.models.Interaction;
import com.outfieldapp.outfieldbackend.models.Notification;
import com.outfieldapp.outfieldbackend.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SyncController {
    private static final String TAG = SyncController.class.getSimpleName();
    private static SyncController instance = new SyncController();
    public static SyncController getInstance() {
        return instance;
    }
    private SyncController() {}

    private boolean isSyncing;
    private int progress;
    private long startTime;
    private int syncTotal;
    private boolean userInfoCurrent;

    public void doSync() {
        if (isSyncing) return;
        if (!userInfoCurrent) {
            getUserDetails().subscribe(success -> doSync());
            return;
        }

        Log.d(TAG, "Starting sync");
        Intent intent = new Intent(Constants.Intents.SYNC_BEGIN_FILTER);
        OutfieldApp.getContext().sendBroadcast(intent);

        isSyncing = true;
        progress = 0;
        syncTotal = 0;
        userInfoCurrent = false;

        Observable.merge(
                syncCurrentUser(),
                syncForms(),
                syncContacts(),
                syncInteractions(),
                syncComments()
        )
                .doOnCompleted(() -> {
                    startTime = System.currentTimeMillis();
                    sync();
                })
                .doOnError(throwable -> onSyncFinished(false))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
    }

    /**
     * Gets up-to-date info about the current user and ensures that the user's account is active.
     */
    private Observable<Boolean> getUserDetails() {
        return OutfieldAPI.getUserDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(user -> {
                    user.save();
                    OutfieldApp.getSharedPrefs().edit()
                            .putBoolean(Constants.Prefs.HAS_TEAM_ACTIVITY, user.hasTeamActivity())
                            .apply();
                    userInfoCurrent = true;
                })
                .map(user -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Updates user's data on the server.
     */
    private Observable<Boolean> syncCurrentUser() {
        final User currentUser = User.getCurrentUser();
        if (currentUser == null || !currentUser.isDirty()) return Observable.just(false);
        return OutfieldAPI.updateUser(currentUser)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(user -> {
                    if (user == null) return;
                    Log.d(TAG, "Updated user on server.");
                    user.setDirty(false);
                    user.setImage(currentUser.getImage());
                    user.save();
                    syncUserImage();
                })
                .map(user -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Updates users image on the server. Should only be called after {@link #syncCurrentUser()}.
     */
    private Observable<Boolean> syncUserImage() {
        // TODO: syncUserImage
        return null;
    }

    /**
     * Sends local contact changes to server and updates local contacts with response data.
     */
    private Observable<Boolean> syncContacts() {
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
                .flatMap(OutfieldAPI::deleteContact)
                .doOnNext(contact -> {
                    if (contact == null) return;
                    Log.d(TAG, "Deleted contact from server");
                    contact.delete();
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
                    if (returnedContact == null) return;
                    Log.d(TAG, "Favored and updated contact on server");
                    returnedContact.setDirty(false);
                    returnedContact.save();
                });

        // Sync created contacts
        Observable<Contact> create = Observable.from(createdContacts)
                .flatMap(contact -> {
                    contact.setId(0);
                    return OutfieldAPI.createContact(contact)
                            .doOnNext(returnedContact -> {
                                if (returnedContact == null) return;
                                Log.d(TAG, "Created contact on server");
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
                    if (returnedContact == null) return;
                    Log.d(TAG, "Updated contact on server");
                    returnedContact.setDirty(false);
                    returnedContact.save();
                });

        // Merge observables into single and return
        return Observable.merge(delete, favor, create, update)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(contact -> true)
                .onErrorReturn(throwable -> false)
                .doOnCompleted(this::syncContactImages);
    }

    /**
     * Sends local interaction changes to server and updates local interactions with response data.
     */
    private Observable<Boolean> syncInteractions() {
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

        // Sort dirty interactions
        while (cursor != null && cursor.moveToNext()) {
            Interaction interaction = new Interaction(cursor);
            long id = interaction.getId();
            if (interaction.isDestroy()) {
                deletedInteractions.add(interaction);
            } else if (id > 0) {
                updatedInteractions.add(interaction);
            } else if (id <= 0) {
                createdInteractions.add(interaction);
            }
        }

        if (cursor != null) cursor.close();

        // Sync deleted interactions
        Observable<Interaction> delete = Observable.from(deletedInteractions)
                .flatMap(OutfieldAPI::deleteInteraction)
                .doOnNext(interaction -> {
                    if (interaction == null) return;
                    Log.d(TAG, "Deleted interaction on server.");
                    interaction.delete();
                });

        // Sync created interactions
        Observable<Interaction> create = Observable.from(createdInteractions)
                .flatMap(interaction -> {
                    interaction.setId(0);
                    return OutfieldAPI.createInteraction(interaction)
                            .doOnNext(returnedInteraction -> {
                                if (returnedInteraction == null) return;
                                Log.d(TAG, "Created interaction on server");
                                interaction.setId(returnedInteraction.getId());
                                interaction.update();
                                returnedInteraction.setImages(interaction.getImages());
                                returnedInteraction.setComments(interaction.getComments());
                                returnedInteraction.setDirty(false);
                                returnedInteraction.save();
                            });
                });

        // Sync updated interactions
        Observable<Interaction> update = Observable.from(updatedInteractions)
                .flatMap(interaction -> OutfieldAPI.updateInteraction(interaction)
                        .doOnNext(returnedInteraction -> {
                            if (returnedInteraction == null) return;
                            Log.d(TAG, "Updated interaction on server");
                            returnedInteraction.setComments(interaction.getComments());
                            returnedInteraction.setImages(interaction.getImages());
                            returnedInteraction.setDirty(false);
                            returnedInteraction.save();
                        }));

        // Merge observables into single and return
        return Observable.merge(delete, create, update)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(interaction -> true)
                .onErrorReturn(throwable -> false)
                .doOnCompleted(this::syncInteractionImages);
    }

    // TODO: syncContactImages
    private void syncContactImages() {
        Log.d(TAG, "SYNC CONTACT IMAGES");
    }

    // TODO: syncInteractionImages
    private void syncInteractionImages() {
        Log.d(TAG, "SYNC INTERACTION IMAGES");
    }

    /**
     * Sends local comment changes to the server and updates database rows with response data.
     */
    private Observable<Boolean> syncComments() {
        List<Comment> deletedComments = new ArrayList<>();
        List<Comment> createdComments = new ArrayList<>();
        List<Comment> updatedComments = new ArrayList<>();

        // Get dirty comments
        SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
        Cursor cursor = db.query(
                OutfieldContract.Comment.TABLE_NAME,
                null,
                OutfieldContract.Comment.DIRTY + "=?",
                new String[]{"1"},
                null, null, null
        );

        // Sort dirty comments
        while (cursor != null && cursor.moveToNext()) {
            Comment comment = new Comment(cursor);
            long id = comment.getId();
            if (comment.isDestroy()) {
                deletedComments.add(comment);
            } else if (id > 0) {
                updatedComments.add(comment);
            } else if (id <= 0) {
                createdComments.add(comment);
            }
        }

        if (cursor != null) cursor.close();

        // Sync deleted comments
        Observable<Comment> delete = Observable.from(deletedComments)
                .flatMap(OutfieldAPI::deleteComment)
                .doOnNext(comment -> {
                    if (comment == null) return;
                    Log.d(TAG, "Deleted comment on server.");
                    comment.delete();
                });

        // Sync created comments
        Observable<Comment> create = Observable.from(createdComments)
                .flatMap(comment -> OutfieldAPI.createComment(comment)
                        .doOnNext(returnedComment -> {
                            if (returnedComment == null) return;
                            Log.d(TAG, "Created comment on server");
                            comment.setId(returnedComment.getId());
                            comment.update();
                            returnedComment.setInteractionId(comment.getInteractionId());
                            returnedComment.setDirty(false);
                            returnedComment.save();
                        }));

        // Sync updated comments
        Observable<Comment> update = Observable.from(updatedComments)
                .flatMap(comment -> OutfieldAPI.updateComment(comment)
                        .doOnNext(returnedComment -> {
                            if (returnedComment == null) return;
                            Log.d(TAG, "Updated comment on server");
                            returnedComment.setInteractionId(comment.getInteractionId());
                            returnedComment.setDirty(false);
                            returnedComment.save();
                        }));

        // Merge observables into single and return
        return Observable.merge(delete, create, update)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(comment -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Retrieves and inserts the 20 most recent notifications from the server
     */
    private Observable<Boolean> syncNotifications() {
        return OutfieldAPI.getNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(notifications -> {
                    if (notifications == null) return;
                    for (Notification notification : notifications) {
                        notification.save();
                    }
                })
                .map(notifications -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Retrieves and inserts organization's current interaction forms.
     */
    private Observable<Boolean> syncForms() {
        Set<String> currentFormIds = new HashSet<>();
        return OutfieldAPI.getLatestForms()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(forms -> {
                    if (forms == null) return;
                    for (Form form : forms) {
                        currentFormIds.add(String.valueOf(form.getId()));
                        form.save();
                    }
                    SharedPreferences.Editor editor = OutfieldApp.getSharedPrefs().edit();
                    editor.putStringSet(Constants.Prefs.CURRENT_FORM_IDS, currentFormIds);
                    editor.apply();
                })
                .map(forms -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Gets contact and interaction changes for favored contacts from server.
     */
    private void sync() {
        SharedPreferences prefs = OutfieldApp.getSharedPrefs();
        final String syncToken = prefs.getString(Constants.Headers.SYNC_TOKEN, null);
        OutfieldAPI.sync(false, 50, syncToken)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(response -> {
                    // Save new sync token
                    String newToken = response.getToken();
                    SharedPreferences.Editor editor = OutfieldApp.getSharedPrefs().edit();
                    editor.putString(Constants.Headers.SYNC_TOKEN, newToken);
                    editor.commit();

                    // If not finished, call sync again
                    if (response.getStatus().equals("more")) {
                        sync();
                    }

                    // Get changes from response
                    List<Contact> contacts = new ArrayList<Contact>();
                    List<Integer> deletedContacts = new ArrayList<Integer>();
                    if (response.getContacts() != null) {
                        contacts.addAll(response.getContacts().getCurrentContacts());
                        deletedContacts.addAll(response.getContacts().getDeletedContactIds());
                    }

                    List<Interaction> interactions = new ArrayList<Interaction>();
                    List<Integer> deletedInteractions = new ArrayList<Integer>();
                    if (response.getInteractions() != null) {
                        interactions.addAll(response.getInteractions().getCurrentInteractions());
                        deletedInteractions.addAll(response.getInteractions().getDeletedInteractionIds());
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

                    // Update progress
                    Intent intent = new Intent(Constants.Intents.SYNC_PROGRESS_FILTER);
                    if (syncTotal == 0) syncTotal = response.getSyncCount();
                    int remainingCount = response.getRemainingCount();
                    progress = syncTotal > 0
                            ? (syncTotal - remainingCount) * 100 / syncTotal
                            : 100;
                    intent.putExtra(Constants.Intents.SYNC_PROGRESS_KEY, progress);

                    // Calculate time remaining
                    long elapsed = System.currentTimeMillis() - startTime;
                    long avgMillisPerItem = elapsed / (syncTotal - remainingCount);
                    long millisRemaining = avgMillisPerItem * remainingCount;
                    if (response.getContactsCount() + response.getInteractionsCount() > 0) {
                        intent.putExtra(Constants.Intents.SYNC_TIME_REMAINING_KEY, millisRemaining);
                    }

                    // Send progress intent
                    OutfieldApp.getContext().sendBroadcast(intent);

                    // Sync notifications and disable loading screen for future app launches
                    if (response.getStatus().equals("done")) {
                        syncNotifications()
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(this::onSyncFinished,
                                        throwable -> onSyncFinished(false));
                        prefs.edit().putBoolean(Constants.Prefs.SHOW_LOADING_SCREEN, false).apply();
                    }
                })
                .doOnError(throwable -> onSyncFinished(false))
                .map(syncResponse -> true)
                .onErrorReturn(throwable -> false)
                .subscribe();
    }

    /**
     * To be called when sync finishes or fails.
     * @param success True if sync completed successfully.
     */
    private void onSyncFinished(boolean success) {
        isSyncing = false;
        Log.d(TAG, "Sync finished. Success = " + success);
        Intent intent = new Intent(Constants.Intents.SYNC_END_FILTER);
        intent.putExtra(Constants.Intents.SYNC_SUCCESS_KEY, success);
        OutfieldApp.getContext().sendBroadcast(intent);
    }

    private void reset() {

    }
}
