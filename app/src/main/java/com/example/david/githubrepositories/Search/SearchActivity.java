package com.example.david.githubrepositories.Search;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.example.david.githubrepositories.ListHistory;
import com.example.david.githubrepositories.R;
import com.example.david.githubrepositories.Result.ResultActivity;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView, View.OnClickListener {
    public ProgressBar progressBar;
    public EditText username;
    public Spinner type;
    private SearchPresenter presenter;
    private  RecyclerView recyclerView;
    private ListHistory adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        username = (EditText) findViewById(R.id.etUserName);
        type = (Spinner) findViewById(R.id.spinner);
        findViewById(R.id.bSearch).setOnClickListener(this);

        presenter = new SearchPresenterImpl(this);
        presenter.takeListHistory();


    }


    @Override
    public void setUsernameError() {
        username.setError("Введите имя пользователя");
    }

    @Override
    public void navigateToResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("userName", username.getText().toString());
        intent.putExtra("ownerType", getType(type));
        startActivity(intent);
    }

    @Override
    public void initSearchRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_History);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Repositories> historyList = new Select(Repositories_Table.user_name, Repositories_Table.owner_type, Repositories_Table.request_time)
                .distinct()
                .from(Repositories.class)
                .where()
                .queryList();
        adapter = new ListHistory(this, historyList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(username.getText())) {
            setUsernameError();
        } else {
            presenter.validateCredentials();
        }
    }

    public String getType(Spinner type) {
        String ownerType = "all";
        if (String.valueOf(type.getSelectedItemPosition()).equals("1"))
            ownerType = "owner";
        else if (String.valueOf(type.getSelectedItemPosition()).equals("2"))
            ownerType = "member";
        return ownerType;
    }
}




