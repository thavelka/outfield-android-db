package com.outfieldapp.outfieldbackend.api;

import com.outfieldapp.outfieldbackend.api.Constants.Endpoints;
import com.outfieldapp.outfieldbackend.api.Constants.Headers;
import com.outfieldapp.outfieldbackend.api.Constants.Params;
import com.outfieldapp.outfieldbackend.api.response.ContactsResponse;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.User;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    class Builder {

        private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        public static ApiService createService() {
            return createService(null, null);
        }

        public static ApiService createService(final String email, final String token) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    if (email != null && token != null) {
                        requestBuilder.header(Headers.EMAIL, email)
                                .header(Headers.AUTH_TOKEN, token);
                    }

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient client = httpClient.build();
            Retrofit retrofit = builder.client(client).build();
            return retrofit.create(ApiService.class);
        }
    }

    /* User Requests */
    @GET(Endpoints.ME)
    Call<User.Wrapper> getUserDetails();

    @GET(Constants.Endpoints.ACCOUNT_EXISTS)
    Call<User.Wrapper> getAccountExists(@Query(Params.AccountExists.EMAIL) String email);

    @POST(Endpoints.SIGN_IN)
    Call<User.Wrapper> signIn(
            @Query(Params.SignIn.EMAIL) String email,
            @Query(Params.SignIn.PASSWORD) String password);

    @POST(Endpoints.SIGN_UP)
    Call<User.Wrapper> signUp(
            @Query(Params.SignUp.EMAIL) String email,
            @Query(Params.SignUp.NAME) String name,
            @Query(Params.SignUp.PASSWORD) String password,
            @Query(Params.SignUp.PASSWORD_CONFIRMATION) String passwordConfirmation,
            @Query(Params.SignUp.ORG_NAME) String organizationName
    );

    @PUT(Endpoints.ME)
    Call<User.Wrapper> updateUser(@Body User.Wrapper user);

    @POST(Endpoints.PASSWORD_RESET)
    Call<Void> resetPassword(@Query(Params.PasswordReset.EMAIL) String email);


    /* Contact Requests */
    @GET(Endpoints.MY_CONTACTS)
    Call<ContactsResponse> getPlaces(
            @Query(Params.Contacts.CONTACT_TYPE) String contactType,
            @Query(Params.Contacts.SEARCH) String search,
            @Query(Params.Contacts.PER_PAGE) Integer perPage,
            @Query(Params.Contacts.PAGE) Integer page,
            @Query(Params.Contacts.GLOBAL) Boolean global,
            @Query(Params.Contacts.LOCATION) String location,
            @Query(Params.Contacts.RADIUS) Integer radius
    );

    @GET(Endpoints.CONTACTS)
    Call<ContactsResponse> getPeople(
            @Query(Params.Contacts.CONTACT_TYPE) String contactType,
            @Query(Params.Contacts.SEARCH) String search,
            @Query(Params.Contacts.PER_PAGE) Integer perPage,
            @Query(Params.Contacts.PAGE) Integer page,
            @Query(Params.Contacts.GLOBAL) Boolean global
    );

    @GET(Endpoints.CONTACTS + "/{id}")
    Call<Contact.Wrapper> getContact(
            @Path("id") Long id
    );

    @GET(Endpoints.EXPLORE)
    Call<ContactsResponse> explore(
            @Query(Params.Explore.LOCATION) String location,
            @Query(Params.Explore.QUERY) String search
    );

    @POST(Endpoints.MY_CONTACTS)
    Call<Contact.Wrapper> favorContact(
            @Query(Params.Contacts.CONTACT_ID) Long contactId
    );

    @POST(Endpoints.MY_CONTACTS)
    Call<Contact.Wrapper> updateAndFavorContact(
            @Body Contact.Wrapper contact
    );

    @POST(Endpoints.CONTACTS)
    Call<Contact.Wrapper> createContact(
            @Body Contact.Wrapper contact
    );

    @PUT(Endpoints.CONTACTS + "/{id}")
    Call<Contact.Wrapper> updateContact(
            @Path("id") Long id,
            @Body Contact.Wrapper contact
    );

    @DELETE(Endpoints.MY_CONTACTS + "/{id}")
    Call<Void> unfavorContact(
            @Path("id") Long id
    );

    @DELETE(Endpoints.CONTACTS + "/{id}")
    Call<Void> deleteContact(
            @Path("id") Long id
    );
}
