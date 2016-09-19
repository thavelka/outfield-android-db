package com.outfieldapp.outfieldbackend.api.response;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.api.Constants;
import com.outfieldapp.outfieldbackend.models.Interaction;

import java.util.ArrayList;
import java.util.List;

public class InteractionsResponse {
    @SerializedName(Constants.Keys.Response.Interactions.INTERACTIONS_COUNT)
    int interactionsCount;
    @SerializedName(Constants.Keys.Response.Interactions.PAGES_COUNT)
    int pagesCount;
    @SerializedName(Constants.Keys.Response.Interactions.PAGE)
    int page;
    @SerializedName(Constants.Keys.Response.Interactions.PER_PAGE)
    int perPage;
    @SerializedName(Constants.Keys.Response.Interactions.INTERACTIONS)
    List<Interaction> interactions = new ArrayList<>();

    public List<Interaction> getInteractions() {
        return interactions;
    }

    public int getInteractionsCount() {
        return interactionsCount;
    }

    public int getPage() {
        return page;
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public int getPerPage() {
        return perPage;
    }
}
