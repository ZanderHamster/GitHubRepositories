package com.example.david.githubrepositories.Result;

import com.example.david.githubrepositories.Database.Repositories;

import java.util.List;

interface ResultView {
    void refreshListRepositories(List<Repositories> repositoriesList);

    void showProgress();

    void hideProgress();

    void initResultRecycler();
}
