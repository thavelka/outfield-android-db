package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interaction extends Model {

    public static final String TAG = Interaction.class.getSimpleName();

    private long rowId;
    private long interactionId;
    private String interactionType;
    private String notes = "";
    private String shareUrl = "";
    private String createdAt = "";
    private boolean inTeamActivity;
    private boolean draft;
    private boolean dirty;
    private boolean destroy;

    User user;
    InteractionDetails interactionDetails = new InteractionDetails();
    List<Contact> contacts = new ArrayList<>();
    List<Long> contactIds = new ArrayList<>();
    List<Form> forms = new ArrayList<>();
    List<Long> formIds = new ArrayList<>();
    List<FormEntryGroup> formEntryGroups = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();
    List<Image> images = new ArrayList<>();

    public Interaction() {}
    public Interaction(Cursor cursor) { if (cursor != null) loadFromCursor(cursor); }

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
    public List<Image> getImage() { return images; }
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


    @Override
    boolean insert() {
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert values, get row id
            rowId = db.insertOrThrow(OutfieldContract.Interaction.TABLE_NAME, null, getContentValues());

            // If contact does not have an id, generate negative id from row id and update
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

    @Override
    void loadFromCursor(Cursor cursor) {
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
    ContentValues getContentValues() {
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
        if (!contacts.isEmpty() && contacts.get(0).getId() != 0) {
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

    private static class InteractionDetails {
        long id;
        float duration;
        List<Location> locations = new ArrayList<>();

        public void setLocation(float latitude, float longitude) {
            locations.set(0, new Location(latitude, longitude));
        }

        public Location getLocation() {
            return (!locations.isEmpty()) ? locations.get(0) : new Location(0,0);
        }

        private static class Location {
            long id;
            float latitude;
            float longitude;

            Location(float latitude, float longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }
    }
}
