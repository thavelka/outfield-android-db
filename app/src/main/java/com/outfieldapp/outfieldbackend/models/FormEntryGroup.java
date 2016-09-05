package com.outfieldapp.outfieldbackend.models;

import java.util.ArrayList;

public class FormEntryGroup {
    private long formId;
    private ArrayList<FormEntry> formEntries;

    /* Getters */
    public long getFormId() { return formId; }
    public ArrayList<FormEntry> getFormEntries() { return formEntries; }

    /* Setters */
    public void setFormId(long id) { formId = id; }
    public void addFormEntry(FormEntry entry) { formEntries.add(entry); }
    public void setFormEntries(ArrayList<FormEntry> entries) { formEntries = entries; }
}
