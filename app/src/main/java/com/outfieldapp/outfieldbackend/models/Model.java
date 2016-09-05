package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class Model {

    abstract boolean insert();
    abstract void loadFromCursor(Cursor cursor);
    abstract ContentValues getContentValues();
}
