package com.outfieldapp.outfieldbackend.api;

import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.api.response.ContactsResponse;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutfieldApi {

    public static final String TAG = OutfieldApi.class.getSimpleName();

    //#############################################################################################
    //                                      USER REQUESTS
    //#############################################################################################

    public interface ResponseCallback<T> {
        public void onResponse(boolean success, T object);
    }

    /**
     * <code>POST /api/v2/sign_in</code>
     * <p>
     * Sends login request to server, which responds with a user object containg the user's info.
     * @param email The user's email address.
     * @param password The user's password.
     * @param callback Callback to receive boolean success value and user object.
     */
    public static void signIn(String email, String password, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = OutfieldApp.getApiService().signIn(email, password);
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during signIn", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/me/new</code>
     * <p>
     * Checks to see if account already exists for this email address. If so, user info is returned.
     * If user doesn't exist, <code>success</code> will be false.
     * @param email The user's email address.
     * @param callback Callback to receive boolean success value and user object.
     */
    public static void getAccountExists(String email, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = OutfieldApp.getApiService().getAccountExists(email);
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during getAccountExists", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/me</code>
     * <p>
     * Retrieves detailed information about user.
     * @param callback Callback to receive boolean success value and user object.
     */
    public static void getUserDetails(final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = OutfieldApp.getApiService().getUserDetails();
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during getUserDetails", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>PUT /api/v2/me</code>
     * <p>
     * Updates user's information.
     * @param user The user object to be sent to the server.
     * @param callback Callback to receive boolean success value and user object.
     */
    public static void updateUser(User user, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = OutfieldApp.getApiService().updateUser(user.wrap());
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during updateUser", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/sign_up</code>
     * <p>
     * Creates a new organization and user on the server.
     * @param email The user's email address.
     * @param name The user's full name.
     * @param password The user's password.
     * @param orgName Name of the organization to be created.
     * @param callback Callback to receive boolean success value and user object.
     */
    public static void signUp(String email, String name, String password,
                              String orgName, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = OutfieldApp.getApiService()
                .signUp(email, name, password, password, orgName);
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during signUp", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/password_reset</code>
     * <p>
     * Sends email to specified address with a link to reset password.
     * @param email The user's email address.
     * @param callback Callback to receive boolean success value.
     */
    public static void resetPassword(String email, final ResponseCallback<Void> callback) {
        Call<Void> call = OutfieldApp.getApiService().resetPassword(email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, null);
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error during resetPassword", t);
                callback.onResponse(false, null);
            }
        });
    }

    // TODO: public static void createPushCredential(String token, ResponseCallback<String> callback)

    //#############################################################################################
    //                                    CONTACT REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/contacts</code>
     * <p>
     * Retrieves one page of people contacts.
     * @param page The page to retrieve. Index begins at 1, not 0. Defaults to 0 if null.
     * @param perPage The number of contacts to retrieve per page. Defauts to 25 if null.
     * @param global Retrieves all places if true, only favored places if false or null.
     * @param search Search contacts for the given query. Can be null.
     * @param callback Callback to receive boolean success value and response.
     */
    public static void getPeople(Integer page, Integer perPage, Boolean global, String search,
                                 final ResponseCallback<ContactsResponse> callback) {
        // Set default parameter values
        page = (page != null) ? page : 1;
        perPage = (perPage != null) ? perPage : 25;
        global = (global != null) ? global : false;

        Call<ContactsResponse> call = OutfieldApp.getApiService()
                .getPeople(Contact.Type.PERSON.toString(), search, perPage, page, global);
        call.enqueue(new Callback<ContactsResponse>() {
            @Override
            public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ContactsResponse> call, Throwable t) {
                Log.e(TAG, "Error during getPeople", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/my_contacts</code>
     * <p>
     * Retrieves one page of place contacts.
     * @param page The page to retrieve. Index begins at 1, not 0. Defaults to 0 if null.
     * @param perPage The number of contacts to retrieve per page. Defauts to 25 if null.
     * @param global Retrieves all places if true, only favored places if false or null.
     * @param search Search contacts for the given query. (optional)
     * @param latitude Latitude as a double, used to get nearby places. (optional)
     * @param longitude Longitude as a double, used to get nearby places. (optional)
     * @param radius Radius in miles, used to get nearby places. (optional)
     * @param callback Callback to receive boolean success value and response.
     */
    public static void getPlaces(Integer page, Integer perPage, Boolean global, String search,
                                 Double latitude, Double longitude, Integer radius,
                                 final ResponseCallback<ContactsResponse> callback) {
        // Set default parameter values
        page = (page != null) ? page : 1;
        perPage = (perPage != null) ? perPage : 25;
        global = (global != null) ? global : false;
        String location = null;
        if (latitude != null && longitude != null) {
            location = latitude + "," + longitude;
        }

        Call<ContactsResponse> call = OutfieldApp.getApiService()
                .getPlaces(Contact.Type.PLACE.toString(), search, perPage, page,
                        global, location, radius);
        call.enqueue(new Callback<ContactsResponse>() {
            @Override
            public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ContactsResponse> call, Throwable t) {
                Log.e(TAG, "Error during getPeople", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/contacts/explore</code>
     * <p>
     * Find all organization contacts and Foursquare venues near the provided coordinates.
     * @param latitude Latitude as a double.
     * @param longitude Longitude as a double.
     * @param search Search for something nearby (e.g. “GNC”). (optional)
     * @param callback Callback to receive boolean success value and contacts array.
     */
    public static void explore(Double latitude, Double longitude, String search,
                               final ResponseCallback<List<Contact>> callback) {
        search = (search != null) ? search : null;
        String location = null;
        if (latitude != null && longitude != null) {
            location = latitude + "," + longitude;
        }
        Call<ContactsResponse> call = OutfieldApp.getApiService()
                .explore(location, search);
        call.enqueue(new Callback<ContactsResponse>() {
            @Override
            public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getContacts());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ContactsResponse> call, Throwable t) {
                Log.e(TAG, "Error during explore", t);
                callback.onResponse(false, null);
            }
        });
    }
}
