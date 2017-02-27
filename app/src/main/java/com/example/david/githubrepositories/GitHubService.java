package com.example.david.githubrepositories;

import com.example.david.githubrepositories.Database.Repositories;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubService {
    @GET("users/{user}/repos")
    Call<List<Repositories>> getRepo(@Path("user") String user, @Query("type") String owner);

    class Factory {
        private static GitHubService service;

        public static GitHubService getInstance() {
            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(GitHubService.class);
                return service;
            } else {
                return service;
            }

        }
    }
}
