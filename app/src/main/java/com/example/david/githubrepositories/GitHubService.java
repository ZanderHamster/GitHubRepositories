package com.example.david.githubrepositories;

import com.example.david.githubrepositories.Database.Repositories;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class GitHubService implements IGitHubService {
    @Override
    public Observable<List<Repositories>> getRepo(@Path("user") final String user, @Query("owner") final String owner) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final IGitHubService service = retrofit.create(IGitHubService.class);
        return service.getRepo(user, owner);
    }
}