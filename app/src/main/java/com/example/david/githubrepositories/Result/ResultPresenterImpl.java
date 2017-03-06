package com.example.david.githubrepositories.Result;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.example.david.githubrepositories.GitHubServiceImpl;
import com.example.david.githubrepositories.Model;
import com.example.david.githubrepositories.ModelImpl;
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
    private Model model;

    ResultPresenterImpl(ResultView resultView) {
        this.resultView = resultView;
        this.model = new ModelImpl();
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
        } else {
            Date requestTime = currentRequest.get(currentRequest.size() - 1).getRequest_time();
            Date currentTime = Calendar.getInstance().getTime();
            long timeDiff = currentTime.getTime() - requestTime.getTime();
            if ((timeDiff / 60000) > 5) {
                makeRequest(username, owner);
            } else {
                resultView.refreshListRepositories(model.getRepositoriesList(username, owner));
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
                        model.loadToDataBase(repositories, username, owner);
                        ClearingHistory();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        resultView.refreshListRepositories(model.getRepositoriesList(username, owner));
                        resultView.hideProgress();
                    }
                });
    }
}
