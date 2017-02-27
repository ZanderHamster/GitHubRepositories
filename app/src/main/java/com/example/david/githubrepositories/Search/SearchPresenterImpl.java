package com.example.david.githubrepositories.Search;

class SearchPresenterImpl implements SearchPresenter, SearchView.OnStartSearch {
    private SearchView searchView;
//    private Model model;

    SearchPresenterImpl(SearchView searchView) {
        this.searchView = searchView;
//        this.model = new ModelImpl();
    }

    @Override
    public void validateCredentials() {
        searchView.showProgress();
        searchView.navigateToResult();
    }

    @Override
    public void onUsernameError() {
        searchView.setUsernameError();
        searchView.hideProgress();
    }

    @Override
    public void onSuccess() {
        searchView.navigateToResult();
    }


}


