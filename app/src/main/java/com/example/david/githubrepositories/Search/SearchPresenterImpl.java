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


    }

    @Override
    public void onUsernameError() {
        searchView.setUsernameError();
    }

    @Override
    public void onSuccess() {
        searchView.navigateToResult();
    }


}


