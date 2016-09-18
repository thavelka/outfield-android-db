package com.outfieldapp.outfieldbackend.models;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.api.Constants.Keys;

import java.util.ArrayList;

public class FormEntryGroup {
    @SerializedName(Keys.Interaction.FormEntryGroup.FORM_ID)
    private long formId;
    @SerializedName(Keys.Interaction.FormEntryGroup.ENTRIES)
    private ArrayList<FormEntry> formEntries;

    /* Getters */
    public long getFormId() { return formId; }
    public ArrayList<FormEntry> getFormEntries() { return formEntries; }

    /* Setters */
    public void setFormId(long id) { formId = id; }
    public void addFormEntry(FormEntry entry) { formEntries.add(entry); }
    public void setFormEntries(ArrayList<FormEntry> entries) { formEntries = entries; }
}
