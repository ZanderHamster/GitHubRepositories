package com.example.david.githubrepositories.Result;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.example.david.githubrepositories.GitHubServiceImpl;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class ResultPresenterImpl implements ResultPresenter {
    private ResultView resultView;

    ResultPresenterImpl(ResultView resultView) {
        this.resultView = resultView;
    }

    @Override
    public void takeListRepositories(String username, String owner) {
        resultView.initResultRecycler();

        List<Repositories> currentRequest = new Select()
                .from(Repositories.class)
                .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(owner))
                .queryList();

        if (currentRequest.isEmpty()) {
            makeRequest(username, owner);
            resultView.refreshListRepositories(getRepositoriesList(username, owner));
        } else {
            Date requestTime = currentRequest.get(currentRequest.size() - 1).getRequest_time();
            Date currentTime = Calendar.getInstance().getTime();
            long timeDiff = currentTime.getTime() - requestTime.getTime();
            if ((timeDiff / 60000) > 5) {
                makeRequest(username, owner);
                resultView.refreshListRepositories(getRepositoriesList(username, owner));
            } else {
                resultView.refreshListRepositories(getRepositoriesList(username, owner));
            }
        }
    }

    private void ClearingHistory() {
        List<Repositories> uniqueUsers = new Select(Repositories_Table.user_name, Repositories_Table.owner_type, Repositories_Table.request_time)
                .distinct()
                .from(Repositories.class)
                .where()
                .queryList();

        if (uniqueUsers.size() > 2) {
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

    @NonNull
    private List<Repositories> getRepositoriesList(String username, String owner) {
        return new Select()
                .from(Repositories.class)
                .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(owner))
                .queryList();
    }

    private void makeRequest(final String username, final String owner) {
        GitHubServiceImpl gitHubService = new GitHubServiceImpl();
        gitHubService.getRepo(username, owner)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Repositories>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        resultView.showProgress();
                    }

                    @Override
                    public void onNext(List<Repositories> repositories) {
                        loadToDataBase(repositories, username, owner);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        resultView.refreshListRepositories(getRepositoriesList(username, owner));
                        resultView.hideProgress();
                    }
                });
    }

    private void loadToDataBase(List<Repositories> repositories, String username, String owner) {
        Date currentTime = Calendar.getInstance().getTime();
        for (int i = 0; i < repositories.size(); i++) {
            Log.d("LOG", repositories.get(i).getName());
            Repositories repo = new Repositories();

            repo.setName(repositories.get(i).getName());
            repo.setDescription(repositories.get(i).getDescription());
            repo.setCreated_at(repositories.get(i).getCreated_at());
            repo.setUpdated_at(repositories.get(i).getUpdated_at());
            repo.setLanguage(repositories.get(i).getLanguage());
            repo.setStargazers_count(repositories.get(i).getStargazers_count());
            repo.setRequest_time(currentTime);
            repo.setUser_name(username);
            repo.setOwner_type(owner);
            repo.save();
        }
        ClearingHistory();
    }
}
