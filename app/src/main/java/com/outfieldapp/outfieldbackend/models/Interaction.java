package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.api.Constants.Keys;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interaction extends Model {

    public static final String TAG = Interaction.class.getSimpleName();

    private long rowId;
    private boolean draft;
    private boolean dirty;

    @SerializedName(Keys.Interaction.ID)
    private long interactionId;
    @SerializedName(Keys.Interaction.INTERACTION_TYPE)
    private String interactionType;
    @SerializedName(Keys.Interaction.NOTES)
    private String notes = "";
    @SerializedName(Keys.Interaction.SHARE_URL)
    private String shareUrl = "";
    @SerializedName(Keys.Interaction.CREATED_AT)
    private String createdAt = "";
    @SerializedName(Keys.Interaction.IN_TEAM_ACTIVITY)
    private boolean inTeamActivity;
    @SerializedName(Keys.Interaction.DESTROY)
    private boolean destroy;

    @SerializedName(Keys.Interaction.USER)
    User user;
    @SerializedName(Keys.Interaction.INTERACTION_DETAILS)
    InteractionDetails interactionDetails = new InteractionDetails();
    @SerializedName(Keys.Interaction.CONTACTS)
    List<Contact> contacts = new ArrayList<>();
    @SerializedName(Keys.Interaction.CONTACT_IDS)
    List<Long> contactIds = new ArrayList<>();
    @SerializedName(Keys.Interaction.FORMS)
    List<Form> forms = new ArrayList<>();
    @SerializedName(Keys.Interaction.FORM_IDS)
    List<Long> formIds = new ArrayList<>();
    @SerializedName(Keys.Interaction.FORM_ENTRIES)
    List<FormEntryGroup> formEntryGroups = new ArrayList<>();
    @SerializedName(Keys.Interaction.COMMENTS)
    List<Comment> comments = new ArrayList<>();
    @SerializedName(Keys.Interaction.IMAGES)
    List<Image> images = new ArrayList<>();

    /* Constructors */
    public Interaction() {}
    public Interaction(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return interactionId; }
    public Type getInteractionType() { return Type.valueOf(interactionType.toUpperCase()); }
    public String getNotes() { return notes; }
    public String getShareUrl() { return shareUrl; }
    public String getCreatedAt() { return createdAt; }
    public boolean isInTeamActivity() { return inTeamActivity; }
    public boolean isDraft() { return draft; }
    public boolean isDirty() { return dirty; }
    public boolean isDestroy() { return destroy; }
    public Contact getContact() { return (!contacts.isEmpty()) ? contacts.get(0) : null; }
    public User getUser() { return user; }
    public List<Form> getForms() { return forms; }
    public List<FormEntryGroup> getFormEntryGroups() { return formEntryGroups; }
    public List<Comment> getComments() { return comments; }
    public List<Image> getImages() { return images; }
    public float getDuration() { return interactionDetails.duration; }
    public float getLatitude() { return interactionDetails.getLocation().latitude; }
    public float getLongitude() { return interactionDetails.getLocation().longitude; }
    public String getFormIdsAsString() {
        if (formIds == null || formIds.isEmpty()) {
            return "";
        } else {
            return TextUtils.join(",", formIds);
        }
    }

    /* Setters */
    public void setId(long id) { interactionId = id; }
    public void setInteractionType(Type type) { interactionType = type.toString(); }
    public void setNotes(String notes) { this.notes = notes;}
    public void setDuration(float duration) { interactionDetails.duration = duration; }
    public void setLocation(float lat, float lng) { interactionDetails.setLocation(lat, lng); }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setInTeamActivity(boolean inTeamActivity) { this.inTeamActivity = inTeamActivity; }
    public void setDraft(boolean draft) { this.draft = draft; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }
    public void setContactId(long id) { contactIds.set(0, id); }
    public void setUser(User user) { this.user = user; }
    public void setFormIds(List<Long> formIds) { this.formIds = formIds; }
    public void setFormEntryGroups(List<FormEntryGroup> groups) { formEntryGroups = groups; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public void setImages(List<Image> images) { this.images = images; }

    /* Database Access */
    /**
     * Searches Interactions database table for a row with matching {@link #interactionId} and uses
     * {@link #loadFromCursor(Cursor)} to create an {@link Interaction} object from that row.
     * @param interactionId The interaction's API id ({@link #interactionId}).
     * @return A completed {@link Interaction} object or null if row could not be found.
     */
    public static Interaction getInteractionWithId(long interactionId) {
        if (interactionId != 0) {
            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
            Cursor interactionCursor = db.query(
                    OutfieldContract.Interaction.TABLE_NAME,
                    null,
                    OutfieldContract.Interaction.INTERACTION_ID + "=?",
                    new String[]{String.valueOf(interactionId)},
                    null,
                    null,
                    null,
                    "1"
            );

            if (interactionCursor != null && interactionCursor.moveToFirst()) {
                Interaction interaction = new Interaction(interactionCursor);
                interactionCursor.close();
                return interaction;
            }
        }

        return null;
    }

    /**
     * Calls {@link #insert()} method for this Interaction object and all submodels. If an
     * Interaction with the same {@link #interactionId} already exists in the database, that
     * Interaction and its submodels will be deleted and replaced.
     * @return True if save was successful.
     */
    public boolean save() {

        if (interactionType.equalsIgnoreCase("planned_check_in")
                || interactionType.equalsIgnoreCase("planned_meeting")) return false;

        // Insert user
        if (user != null) user.save();

        // Insert contact
        if (getContact() != null) {
            getContact().save();
        }

        // Insert forms
        for (Form form : forms) {
            form.save();
        }

        // Insert interaction
        insert();

        // Insert form entries
        for (FormEntryGroup group : formEntryGroups) {
            for (FormEntry entry : group.getFormEntries()) {
                entry.setInteractionId(interactionId);
                entry.setFormId(group.getFormId());
                entry.insert();
            }
        }

        // Insert comments
        for (Comment comment : comments) {
            comment.setInteractionId(interactionId);
            comment.save();
        }

        // Insert images
        for (Image image : images) {
            image.setInteractionId(interactionId);
            image.insert();
        }

        return true;
    }

    @Override
    protected boolean insert() {

        if (contacts.isEmpty() && contactIds.isEmpty()) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert values, get row id
            rowId = db.insertOrThrow(OutfieldContract.Interaction.TABLE_NAME, null, getContentValues());

            // If interaction does not have an id, generate negative id from row id and update
            if (interactionId == 0) {
                interactionId = rowId * -1;
                ContentValues values = new ContentValues();
                values.put(OutfieldContract.Interaction.INTERACTION_ID, interactionId);
                db.update(
                        OutfieldContract.Interaction.TABLE_NAME,
                        values,
                        OutfieldContract.Interaction._ID + "=?",
                        new String[]{String.valueOf(rowId)}
                );
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error during insert()", e);
        } finally {
            db.endTransaction();
        }
        return rowId >= 0;
    }

    public boolean update() {

        if (rowId <= 0) {
            Log.e(TAG, "Error: You must insert before updating");
            return false;
        }
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        int rows = db.update(
                OutfieldContract.Interaction.TABLE_NAME,
                getContentValues(),
                OutfieldContract.Interaction._ID + "=?",
                new String[]{String.valueOf(rowId)}
        );
        db.setTransactionSuccessful();
        db.endTransaction();
        return rows > 0;
    }

    public boolean delete() {
        if (rowId <= 0) return false;
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        int rows = db.delete(
                OutfieldContract.Interaction.TABLE_NAME,
                OutfieldContract.Interaction._ID + "=?",
                new String[]{String.valueOf(rowId)}
        );
        return rows > 0;
    }

    @Override
    protected void loadFromCursor(Cursor cursor) {
        try {
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction._ID);
            int interactionIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.INTERACTION_ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.USER_ID);
            int contactIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.CONTACT_ID);
            int formIdsIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.FORM_IDS);
            int typeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.INTERACTION_TYPE);
            int notesIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.NOTES);
            int shareUrlIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.SHARE_URL);
            int createdAtIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.CREATED_AT);
            int teamActivityIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.IN_TEAM_ACTIVITY);
            int draftIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.DRAFT);
            int dirtyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.DIRTY);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.DESTROY);
            int durationIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.DURATION);
            int latitudeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.LATITUDE);
            int longitudeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Interaction.LONGITUDE);

            // Load interaction values
            rowId = cursor.getLong(rowIndex);
            interactionId = cursor.getLong(interactionIdIndex);
            interactionType = cursor.getString(typeIndex);
            notes = cursor.getString(notesIndex);
            shareUrl = cursor.getString(shareUrlIndex);
            createdAt = cursor.getString(createdAtIndex);
            inTeamActivity = cursor.getInt(teamActivityIndex) > 0;
            draft = cursor.getInt(draftIndex) > 0;
            dirty = cursor.getInt(dirtyIndex) > 0;
            destroy = cursor.getInt(destroyIndex) > 0;

            // Load interaction detail values
            interactionDetails.duration = cursor.getFloat(durationIndex);
            interactionDetails.setLocation(cursor.getFloat(latitudeIndex), cursor.getFloat(longitudeIndex));

            // Load user
            long userId = cursor.getLong(userIdIndex);
            if (userId != 0) user = User.getUserWithId(userId);

            // Load contact
            long contactId = cursor.getLong(contactIdIndex);
            if (contactId != 0) {
                contactIds.set(0, contactId);
                contacts.set(0, Contact.getContactWithId(contactId));
            }

            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();

            // Load comments
            comments.clear();
            Cursor commentCursor = db.query(
                    OutfieldContract.Comment.TABLE_NAME,
                    null,
                    OutfieldContract.Comment.INTERACTION_ID + "=?",
                    new String[]{String.valueOf(interactionId)},
                    null,
                    null,
                    OutfieldContract.Comment.CREATED_AT
            );

            while (commentCursor != null && commentCursor.moveToNext()) {
                comments.add(new Comment(commentCursor));
            }

            if (commentCursor != null) commentCursor.close();

            // Load images
            images.clear();
            Cursor imageCursor = db.query(
                    OutfieldContract.Image.TABLE_NAME,
                    null,
                    OutfieldContract.Image.INTERACTION_ID + "=?",
                    new String[]{String.valueOf(interactionId)},
                    null,
                    null,
                    null
            );

            while (imageCursor != null && imageCursor.moveToNext()) {
                images.add(new Image(imageCursor));
            }

            if (imageCursor != null) imageCursor.close();

            // Load forms
            formIds.clear();
            forms.clear();
            Map<Long, FormEntryGroup> groupMap = new HashMap<>();
            String formIdString = cursor.getString(formIdsIndex);
            List<String> formIdStringArray = new ArrayList<>();
            Collections.addAll(formIdStringArray, TextUtils.split(formIdString, ","));

            for (String s : formIdStringArray) {
                long formId = Long.parseLong(s);
                Form form = Form.getFormWithId(formId);
                if (form != null) {
                    formIds.add(formId);
                    forms.add(form);
                    FormEntryGroup group = new FormEntryGroup();
                    group.setFormId(formId);
                    groupMap.put(formId, group);
                }
            }

            // Load entries
            Cursor entryCursor = db.query(
                    OutfieldContract.FormEntry.TABLE_NAME,
                    null,
                    OutfieldContract.FormEntry.INTERACTION_ID + "=?",
                    new String[]{String.valueOf(interactionId)},
                    null,
                    null,
                    null
            );

            while (entryCursor != null && entryCursor.moveToNext()) {
                FormEntry entry = new FormEntry(entryCursor);
                FormEntryGroup group = groupMap.get(entry.getFormId());
                if (group != null) group.addFormEntry(entry);
            }

            if (entryCursor != null) entryCursor.close();


        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        // Put interaction values
        if (interactionId != 0) values.put(OutfieldContract.Interaction.INTERACTION_ID, interactionId);
        values.put(OutfieldContract.Interaction.INTERACTION_TYPE, interactionType);
        values.put(OutfieldContract.Interaction.NOTES, notes);
        values.put(OutfieldContract.Interaction.SHARE_URL, shareUrl);
        values.put(OutfieldContract.Interaction.CREATED_AT, createdAt);
        values.put(OutfieldContract.Interaction.IN_TEAM_ACTIVITY, inTeamActivity);
        values.put(OutfieldContract.Interaction.DRAFT, draft);
        values.put(OutfieldContract.Interaction.DIRTY, dirty);
        values.put(OutfieldContract.Interaction.DESTROY, destroy);

        // Put interaction detail values
        if (Type.valueOf(interactionType.toUpperCase()) != Type.NOTE) {
            values.put(OutfieldContract.Interaction.DURATION, getDuration());
            values.put(OutfieldContract.Interaction.LATITUDE, getLatitude());
            values.put(OutfieldContract.Interaction.LONGITUDE, getLongitude());
        }

        // Put contact values
        if (!contactIds.isEmpty()) {
            values.put(OutfieldContract.Interaction.CONTACT_ID, contactIds.get(0));
        } else if (!contacts.isEmpty() && contacts.get(0).getId() != 0) {
            values.put(OutfieldContract.Interaction.CONTACT_ID, contacts.get(0).getId());
        }

        // Put user values
        if (user != null && user.getId() != 0) {
            values.put(OutfieldContract.Interaction.USER_ID, user.getId());
        }

        // Put form values
        if (!formIds.isEmpty()) {
            values.put(OutfieldContract.Interaction.FORM_IDS, getFormIdsAsString());
        }

        // Put comment values
        if (!comments.isEmpty()) {
            values.put(OutfieldContract.Interaction.COMMENT_COUNT, comments.size());
        }

        // Put image values
        if (!images.isEmpty()) {
            values.put(OutfieldContract.Interaction.IMAGE_COUNT, images.size());
            values.put(OutfieldContract.Interaction.IMAGE_URL, images.get(0).getOriginalUrlString());
        }

        return values;
    }

    public enum Type {
        CHECK_IN, MEETING, NOTE;

        @Override
        public String toString() { return name().toLowerCase(); }

        public String getPrettyName() {
            switch (this) {
                case CHECK_IN:
                    return "check-in";
                default:
                    return toString();
            }
        }
    }

    /**
     * Wrapper class required for correct JSON serialization and deserialization of individual
     * objects. Wrap single objects with wrap() when creating POST payloads for API requests.
     */
    public static class Wrapper {
        @SerializedName(Keys.Interaction.CLASS_NAME)
        private Interaction interaction;

        public Wrapper(Interaction interaction) {
            this.interaction = interaction;
        }

        public Interaction getInteraction() {
            return interaction;
        }
    }

    public Wrapper wrap() {
        return new Wrapper(this);
    }

    private static class InteractionDetails {
        @SerializedName(Keys.Interaction.InteractionDetails.ID)
        long id;
        @SerializedName(Keys.Interaction.InteractionDetails.EDITED_DURATION)
        float duration;
        @SerializedName(Keys.Interaction.InteractionDetails.PATH)
        List<Location> locations = new ArrayList<>();

        public void setLocation(float latitude, float longitude) {
            locations.set(0, new Location(latitude, longitude));
        }

        public Location getLocation() {
            return (!locations.isEmpty()) ? locations.get(0) : new Location(0,0);
        }

        private static class Location {
            long id;
            @SerializedName(Keys.Interaction.InteractionDetails.Location.LATITUDE)
            float latitude;
            @SerializedName(Keys.Interaction.InteractionDetails.Location.LONGITUDE)
            float longitude;

            Location(float latitude, float longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }
    }
}
