package com.example.david.githubrepositories;

import com.example.david.githubrepositories.Database.Repositories;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubService {
    @GET("users/{user}/repos")
    Observable<List<Repositories>> getRepo(@Path("user") String user, @Query("owner") String owner);
}
