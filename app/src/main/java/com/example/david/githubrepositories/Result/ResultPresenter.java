package com.example.david.githubrepositories.Result;

import android.util.Log;
import android.widget.Toast;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.example.david.githubrepositories.GitHubService;
import com.example.david.githubrepositories.Model.IModel;
import com.example.david.githubrepositories.Model.Model;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class ResultPresenter implements IResultPresenter {
    private IResultView resultView;
    private IModel IModel;

    ResultPresenter(IResultView resultView) {
        this.resultView = resultView;
        this.IModel = new Model();
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
                resultView.refreshListRepositories(IModel.getRepositoriesList(username, owner));
            }
        }
    }


    private void makeRequest(final String username, final String owner) {
        GitHubService gitHubService = new GitHubService();
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
                        IModel.loadToDataBase(repositories, username, owner);
                        IModel.ClearingHistory();
                    }

                    @Override
                    public void onError(Throwable e) {
                        resultView.hideProgress();
                        resultView.requestError();
                    }

                    @Override
                    public void onComplete() {
                        resultView.refreshListRepositories(IModel.getRepositoriesList(username, owner));
                        resultView.hideProgress();
                    }
                });
    }
}
