package com.example.david.githubrepositories;

import android.widget.EditText;
import android.widget.Spinner;

public interface SearchView {
    EditText getButton();

    Spinner getSpinner();

    void setUsernameError();

    void navigateToResult();

    interface OnStartSearch {
        void onUsernameError();

        void onSuccess();
    }
}
