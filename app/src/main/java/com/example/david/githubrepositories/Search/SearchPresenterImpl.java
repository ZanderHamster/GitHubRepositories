package com.example.david.githubrepositories.Search;

class SearchPresenterImpl implements SearchPresenter {
    private SearchView searchView;

    SearchPresenterImpl(SearchView searchView) {
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


