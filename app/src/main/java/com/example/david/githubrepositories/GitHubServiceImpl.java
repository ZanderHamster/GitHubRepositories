package com.example.david.githubrepositories;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class GitHubServiceImpl implements GitHubService {
    @Override
    public Observable<List<Repositories>> getRepo(@Path("user") String user, @Query("type") String owner) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubService service = retrofit.create(GitHubService.class);

//        List<Repositories> list = new Select()
//                .from(Repositories.class)
//                .where(Repositories_Table.user_name.is("zanderhamster"), Repositories_Table.owner_type.is("all"))
//                .queryList();

        return service.getRepo(user, owner);
//        return Observable.fromArray(list);
    }
}
//class Factory {
//    private static GitHubService service;
//
//    public static GitHubService getInstance() {
//        if (service == null) {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("https://api.github.com/")
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            service = retrofit.create(GitHubService.class);
//            return service;
//        } else {
//            return service;
//        }
//
//    }
//}