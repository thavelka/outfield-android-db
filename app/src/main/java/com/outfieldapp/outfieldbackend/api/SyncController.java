package com.outfieldapp.outfieldbackend.api;

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
            getUserDetails().subscribe(success -> doSync());
            return;
        }

        Log.d(TAG, "Starting sync");
        // TODO: Send broadcast intent

        isSyncing = true;
        progress = 0;
        syncTotal = 0;
        userInfoCurrent = false;

        Single.merge(
                syncCurrentUser(),
                syncForms(),
                syncContacts(),
                syncInteractions(),
                syncComments(),
                syncNotifications()
        )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(success -> {
                    SharedPreferences prefs = OutfieldApp.getSharedPrefs();
                    String syncToken = prefs.getString(Constants.Headers.SYNC_TOKEN, null);
                    sync(false, syncToken);
                }, throwable -> onSyncFinished(false));
    }

    /**
     * Gets up-to-date info about the current user and ensures that the user's account is active.
     */
    private Single<Boolean> getUserDetails() {
        return OutfieldAPI.getUserDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(user -> {
                    user.save();
                    hasTeamActivity = user.hasTeamActivity();
                    userInfoCurrent = true;
                })
                .map(user -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Updates user's data on the server.
     */
    private Single<Boolean> syncCurrentUser() {
        final User currentUser = User.getCurrentUser();
        if (currentUser == null || !currentUser.isDirty()) return Single.just(false);
        return OutfieldAPI.updateUser(currentUser)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(user -> {
                    if (user != null) {
                        user.setDirty(false);
                        user.setImage(currentUser.getImage());
                        user.save();
                        Log.d(TAG, "Updated user on server.");
                    }
                })
                .map(user -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Updates users image on the server. Should only be called after {@link #syncCurrentUser()}.
     */
    private Single<Boolean> syncUserImage() {
        // TODO: syncUserImage
        return null;
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
                .flatMap(contact -> OutfieldAPI.deleteContact(contact).toObservable())
                .doOnNext(contact -> {
                    if (contact != null) {
                        Log.d(TAG, "Deleted contact from server");
                        contact.delete();
                    }
                });

        // Favor and update contacts
        Observable<Contact> favor = Observable.from(favoredContacts)
                .flatMap(contact -> {
                    if (contact.getContactType() == Contact.Type.PLACE) {
                        contact.setImages(new ArrayList<>());
                    }
                    return OutfieldAPI.updateAndFavorContact(contact).toObservable();
                })
                .doOnNext(returnedContact -> {
                    if (returnedContact != null) {
                        Log.d(TAG, "Favored and updated contact on server");
                        returnedContact.setDirty(false);
                        returnedContact.save();
                    }
                });

        // Sync created contacts
        Observable<Contact> create = Observable.from(createdContacts)
                .flatMap(contact -> {
                    contact.setId(0);
                    return OutfieldAPI.createContact(contact).toObservable()
                            .doOnNext(returnedContact -> {
                                if (returnedContact != null) {
                                    Log.d(TAG, "Created contact on server");
                                    contact.setId(returnedContact.getId());
                                    contact.update();
                                    returnedContact.setImages(contact.getImages());
                                    returnedContact.setDirty(false);
                                    returnedContact.save();
                                }
                            });
                });

        // Sync updated contacts
        Observable<Contact> update = Observable.from(updatedContacts)
                .flatMap(contact -> {
                    if (contact.getContactType() == Contact.Type.PLACE) {
                        contact.setImages(new ArrayList<>());
                    }
                    return OutfieldAPI.updateContact(contact).toObservable();
                })
                .doOnNext(returnedContact -> {
                    Log.d(TAG, "Updated contact on server");
                    returnedContact.setDirty(false);
                    returnedContact.save();
                });

        // Merge observables into single and return
        return Single.create(singleSubscriber ->
                Observable.merge(delete, favor, create, update)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnCompleted(() -> {
                    syncContactImages();
                    singleSubscriber.onSuccess(true);
                })
                .subscribe());
    }

    /**
     * Sends local interaction changes to server and updates local interactions with response data.
     */
    private Single<Boolean> syncInteractions() {
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
                .flatMap(interaction -> OutfieldAPI.deleteInteraction(interaction).toObservable())
                .doOnNext(interaction -> {
                    if (interaction != null) {
                        Log.d(TAG, "Deleted interaction on server.");
                        interaction.delete();
                    }
                });

        // Sync created interactions
        Observable<Interaction> create = Observable.from(createdInteractions)
                .flatMap(interaction -> {
                    interaction.setId(0);
                    return OutfieldAPI.createInteraction(interaction).toObservable()
                            .doOnNext(returnedInteraction -> {
                                if (returnedInteraction != null) {
                                    Log.d(TAG, "Created interaction on server");
                                    interaction.setId(returnedInteraction.getId());
                                    interaction.update();
                                    returnedInteraction.setImages(interaction.getImages());
                                    returnedInteraction.setComments(interaction.getComments());
                                    returnedInteraction.setDirty(false);
                                    returnedInteraction.save();
                                }
                            });
                });

        // Sync updated interactions
        Observable<Interaction> update = Observable.from(updatedInteractions)
                .flatMap(interaction -> OutfieldAPI.updateInteraction(interaction).toObservable()
                        .doOnNext(returnedInteraction -> {
                            Log.d(TAG, "Updated interaction on server");
                            returnedInteraction.setComments(interaction.getComments());
                            returnedInteraction.setImages(interaction.getImages());
                            returnedInteraction.setDirty(false);
                            returnedInteraction.save();
                        }));

        // Merge observables into single and return
        return Single.create(singleSubscriber ->
                Observable.merge(delete, create, update)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .doOnCompleted(() -> {
                            syncInteractionImages();
                            singleSubscriber.onSuccess(true);
                        })
                        .subscribe());
    }

    private void syncContactImages() {
        Log.d(TAG, "SYNC CONTACT IMAGES");
    }

    private void syncInteractionImages() {
        Log.d(TAG, "SYNC INTERACTION IMAGES");
    }

    private Single<Boolean> syncComments() {
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
                .flatMap(comment -> OutfieldAPI.deleteComment(comment).toObservable())
                .doOnNext(comment -> {
                    if (comment != null) {
                        Log.d(TAG, "Deleted comment on server.");
                        comment.delete();
                    }
                });

        // Sync created comments
        Observable<Comment> create = Observable.from(createdComments)
                .flatMap(comment -> OutfieldAPI.createComment(comment).toObservable()
                        .doOnNext(returnedComment -> {
                            if (returnedComment != null) {
                                Log.d(TAG, "Created comment on server");
                                comment.setId(returnedComment.getId());
                                comment.update();
                                returnedComment.setInteractionId(comment.getInteractionId());
                                returnedComment.setDirty(false);
                                returnedComment.save();
                            }
                        }));

        // Sync updated comments
        Observable<Comment> update = Observable.from(updatedComments)
                .flatMap(comment -> OutfieldAPI.updateComment(comment).toObservable()
                        .doOnNext(returnedComment -> {
                            Log.d(TAG, "Updated comment on server");
                            returnedComment.setInteractionId(comment.getInteractionId());
                            returnedComment.setDirty(false);
                            returnedComment.save();
                        }));

        // Merge observables into single and return
        return Single.create(singleSubscriber ->
                Observable.merge(delete, create, update)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .doOnCompleted(() -> singleSubscriber.onSuccess(true))
                        .subscribe());
    }

    private Single<Boolean> syncNotifications() {
        return OutfieldAPI.getNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(notifications -> {
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
    private Single<Boolean> syncForms() {
        Set<String> currentFormIds = new HashSet<>();
        return OutfieldAPI.getLatestForms()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(forms -> {
                    if (forms != null) {
                        for (Form form : forms) {
                            currentFormIds.add(String.valueOf(form.getId()));
                            form.save();
                        }
                        SharedPreferences.Editor editor = OutfieldApp.getSharedPrefs().edit();
                        editor.putStringSet(Constants.Prefs.CURRENT_FORM_IDS, currentFormIds);
                        editor.apply();
                    }
                })
                .map(forms -> true)
                .onErrorReturn(throwable -> false);
    }

    /**
     * Gets contact and interaction changes for favored contacts from server.
     * @param onlyMe If false, retrieves interactions by all team members for favored contact.
     * @param syncToken When to begin syncing from. If null, will sync from beginning of time.
     */
    private Single<Boolean> sync(final Boolean onlyMe, final String syncToken) {

        final String oldToken = this.syncToken;
        this.syncToken = syncToken;
        return OutfieldAPI.sync(onlyMe, 50, syncToken)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(syncResponse -> {
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
                })
                .doOnError(throwable -> {
                    setSyncToken(oldToken);
                    onSyncFinished(false);
                })
                .map(syncResponse -> true)
                .onErrorReturn(throwable -> false);
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
