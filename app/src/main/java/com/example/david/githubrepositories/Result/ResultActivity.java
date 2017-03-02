package com.example.david.githubrepositories.Result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.example.david.githubrepositories.ListRepositories;
import com.example.david.githubrepositories.R;
import com.example.david.githubrepositories.Search.SearchActivity;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

public class ResultActivity extends AppCompatActivity implements ResultView {
    private List<Repositories> repositoriesList;
    private RecyclerView recyclerView;
    private String userName;
    private String ownerType;
    private ProgressBar progressBar;
    private ListRepositories adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        adapter = new ListRepositories(repositoriesList);

        ResultPresenter presenter = new ResultPresenterImpl(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        ownerType = intent.getStringExtra("ownerType");

        presenter.takeListRepositories(userName,ownerType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.name_ascending:
                nameAscending();
                break;
            case R.id.name_descending:
                nameDescending();
                break;
            case R.id.date_ascending:
                dateAscending();
                break;
            case R.id.date_descending:
                dateDescending();
                break;
        }
        return true;
    }

    public void nameDescending() {
        if (!TextUtils.isEmpty(ownerType) && !TextUtils.isEmpty(userName)) {
            repositoriesList = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(userName), Repositories_Table.owner_type.is(ownerType))
                    .orderBy(Repositories_Table.name, false)
                    .queryList();
            updateList(repositoriesList);
        }
    }

    public void nameAscending() {
        if (!TextUtils.isEmpty(ownerType) && !TextUtils.isEmpty(userName)) {
            repositoriesList = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(userName), Repositories_Table.owner_type.is(ownerType))
                    .orderBy(Repositories_Table.name, true)
                    .queryList();
            updateList(repositoriesList);
        }
    }

    public void dateAscending() {
        if (!TextUtils.isEmpty(ownerType) && !TextUtils.isEmpty(userName)) {
            repositoriesList = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(userName), Repositories_Table.owner_type.is(ownerType))
                    .orderBy(Repositories_Table.created_at, true)
                    .queryList();
            updateList(repositoriesList);
        }
    }

    public void dateDescending() {
        if (!TextUtils.isEmpty(ownerType) && !TextUtils.isEmpty(userName)) {
            repositoriesList = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(userName), Repositories_Table.owner_type.is(ownerType))
                    .orderBy(Repositories_Table.created_at, false)
                    .queryList();
            updateList(repositoriesList);
        }
    }

    @Override
    public void updateList(List<Repositories> repositoriesList) {
        adapter.setRepositories(repositoriesList);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void initResultRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.rvRepositories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
