package com.example.david.githubrepositories.Result;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Adapter.ListRepositoriesAdapter;
import com.example.david.githubrepositories.R;
import com.example.david.githubrepositories.Search.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements IResultView {
    private String userName;
    private String ownerType;
    private ProgressBar progressBar;
    private ListRepositoriesAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        adapter = new ListRepositoriesAdapter(new ArrayList<>());

        IResultPresenter presenter = new ResultPresenter(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        userName = getIntent().getStringExtra("userName");
        ownerType = getIntent().getStringExtra("ownerType");

        presenter.takeListRepositories(userName, ownerType);
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
                adapter.sortNameAscending(userName, ownerType);
                break;
            case R.id.name_descending:
                adapter.sortNameDescending(userName, ownerType);
                break;
            case R.id.date_ascending:
                adapter.sortDateAscending(userName, ownerType);
                break;
            case R.id.date_descending:
                adapter.sortDateDescending(userName, ownerType);
                break;
        }
        return true;
    }

    @Override
    public void refreshListRepositories(List<Repositories> repositoriesList) {
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvRepositories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void requestError() {
        Toast.makeText(this, R.string.error,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SearchActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
