package com.example.david.githubrepositories;

import android.text.TextUtils;
import android.util.Log;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.DefaultSubscriber;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

public class ModelImpl implements Model {
    private String username;
    private String type;

    public ModelImpl(String username, String type) {
        this.username = username;
        this.type = type;


    }

    @Override
    public List<Repositories> requestToGitHub() {


        if (TextUtils.isEmpty(username)) {
            return getRepositoriesList(username, type);
        } else {
            List<Repositories> currentRequest = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(type))
                    .queryList();
            if (currentRequest.isEmpty()) {
                GitHubServiceImpl gitHubService = new GitHubServiceImpl();
                Observable<List<Repositories>> data = gitHubService.getRepo(username, type)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());
                data.subscribe(new Observer<List<Repositories>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("LOG","onSubscribe");
                    }

                    @Override
                    public void onNext(List<Repositories> repositories) {
                        Log.d("LOG","onNext");
                        for (int i = 0; i <repositories.size(); i++) {
                            Log.d("LOG",repositories.get(i).getName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("LOG",e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("LOG","Complete");
                    }
                });

//                Call<List<Repositories>> call = GitHubService
//                        .Factory
//                        .getInstance()
//                        .getRepo(username, type);
//                makeEnqueue(call, username, type);
                return getRepositoriesList(username, type);
            } else {
                Date requestTime = currentRequest.get(currentRequest.size() - 1).getRequest_time();
                Date currentTime = Calendar.getInstance().getTime();
                long timeDiff = currentTime.getTime() - requestTime.getTime();
                if ((timeDiff / 60000) > 5) {
//                    Call<List<Repositories>> call = GitHubService
//                            .Factory
//                            .getInstance()
//                            .getRepo(username, type);
//                    makeEnqueue(call, username, type);
                    return getRepositoriesList(username, type);
                } else {
                    return getRepositoriesList(username, type);
                }
            }
        }

    }

    public void makeEnqueue(Call<List<Repositories>> call, final String username, final String owner) {
        call.enqueue(new Callback<List<Repositories>>() {
            @Override
            public void onResponse(Call<List<Repositories>> call, Response<List<Repositories>> response) {
                Date currentTime = Calendar.getInstance().getTime();
                for (int i = 0; i < response.body().size(); i++) {
                    Repositories repositories = new Repositories();

                    repositories.setName(response.body().get(i).getName());
                    repositories.setDescription(response.body().get(i).getDescription());
                    repositories.setCreated_at(response.body().get(i).getCreated_at());
                    repositories.setUpdated_at(response.body().get(i).getUpdated_at());
                    repositories.setLanguage(response.body().get(i).getLanguage());
                    repositories.setStargazers_count(response.body().get(i).getStargazers_count());
                    repositories.setRequest_time(currentTime);
                    repositories.setUser_name(username);
                    repositories.setOwner_type(owner);
                    repositories.save();
                }
                ClearingHistory();
            }

            @Override
            public void onFailure(Call<List<Repositories>> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });

    }

    public void ClearingHistory() {
        List<Repositories> uniqueUsers = new Select(Repositories_Table.user_name, Repositories_Table.owner_type, Repositories_Table.request_time)
                .distinct()
                .from(Repositories.class)
                .where()
                .queryList();

        if (uniqueUsers.size() >= 2) {
            Repositories oldestRequestTime = new Select(Method.min(Repositories_Table.request_time), Repositories_Table.request_time)
                    .from(Repositories.class)
                    .where()
                    .querySingle();
            List<Repositories> forDelete = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.request_time.is(oldestRequestTime != null ? oldestRequestTime.getRequest_time() : null))
                    .queryList();
            for (int i = 0; i < forDelete.size(); i++) {
                forDelete.get(i).delete();
            }
        }
    }

    private List<Repositories> getRepositoriesList(String username, String type) {
        return new Select()
                .from(Repositories.class)
                .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(type))
                .queryList();
    }
}
