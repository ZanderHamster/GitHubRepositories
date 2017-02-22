package com.example.david.githubrepositories.Search;

import android.widget.EditText;
import android.widget.Spinner;

public interface SearchView {

    void setUsernameError();

    void navigateToResult();

    interface OnStartSearch {
        void onUsernameError();

        void onSuccess();
    }
}
