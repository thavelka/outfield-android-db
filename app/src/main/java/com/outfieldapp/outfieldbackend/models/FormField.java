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

public class FormField extends Model {

    public static final String TAG = FormField.class.getSimpleName();

    private long rowId;

    @SerializedName(Keys.Form.FormField.ID)
    private long formFieldId;
    @SerializedName(Keys.Form.FormField.FORM_ID)
    private long formId;
    @SerializedName(Keys.Form.FormField.POSITION)
    private int position;
    @SerializedName(Keys.Form.FormField.IS_REQUIRED)
    private boolean required;
    @SerializedName(Keys.Form.FormField.LABEL)
    private String label;
    @SerializedName(Keys.Form.FormField.FIELD_TYPE)
    private String fieldType;
    @SerializedName(Keys.Form.FormField.CHOICES)
    private ArrayList<String> choices;

    /* Constructors */
    public FormField() {}
    public FormField(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return formFieldId; }
    public long getFormId() { return formId; }
    public int getPosition() { return position; }
    public boolean isRequired() { return required; }
    public String getLabel() { return label; }
    public FieldType getFieldType() { return FieldType.valueOf(fieldType.toUpperCase()); }
    public ArrayList<String> getChoices() { return choices; }
    public String getChoicesAsString() {
        if (choices == null || choices.isEmpty()) {
            return "";
        } else {
            return TextUtils.join(",", choices);
        }
    }

    /* Database Access */
    @Override
    protected boolean insert() {

        if (formId == 0) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.FormField.TABLE_NAME, null, getContentValues());
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error during insert()", e);
        } finally {
            db.endTransaction();
        }
        return rowId >= 0;
    }

    @Override
    protected void loadFromCursor(Cursor cursor) {
        try {
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField._ID);
            int fieldIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField.FORM_FIELD_ID);
            int formIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField.FORM_ID);
            int positionIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField.POSITION);
            int requiredIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField.REQUIRED);
            int labelIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField.LABEL);
            int fieldTypeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField.FIELD_TYPE);
            int choicesIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormField.CHOICES);

            rowId = cursor.getLong(rowIndex);
            formFieldId = cursor.getLong(fieldIdIndex);
            formId = cursor.getLong(formIdIndex);
            position = cursor.getInt(positionIndex);
            required = cursor.getInt(requiredIndex) > 0;
            label = cursor.getString(labelIndex);
            fieldType = cursor.getString(fieldTypeIndex);

            choices = new ArrayList<>();
            String choicesString = cursor.getString(choicesIndex);
            Collections.addAll(choices, TextUtils.split(choicesString, ","));

        } catch (Exception e) {
            Log.d(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (formFieldId != 0) values.put(OutfieldContract.FormField.FORM_FIELD_ID, formFieldId);
        if (formId != 0) values.put(OutfieldContract.FormField.FORM_ID, formId);
        values.put(OutfieldContract.FormField.POSITION, position);
        values.put(OutfieldContract.FormField.REQUIRED, required);
        values.put(OutfieldContract.FormField.LABEL, label);
        values.put(OutfieldContract.FormField.FIELD_TYPE, fieldType);
        values.put(OutfieldContract.FormField.CHOICES, getChoicesAsString());
        return values;
    }

    public enum FieldType {
        CHECKBOX, CHOICE_LIST, TEXT_FIELD, TEXT_AREA;
        @Override
        public String toString() { return name().toLowerCase(); }
    }
}
