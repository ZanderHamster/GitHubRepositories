package com.example.david.githubrepositories.Search;

class SearchPresenter implements ISearchPresenter {
    private ISearchView searchView;

    SearchPresenter(ISearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void validateCredentials() {
        searchView.navigateToResult();
    }

    @Override
    public void takeListHistory() {
        searchView.initSearchRecycler();
    }
}


