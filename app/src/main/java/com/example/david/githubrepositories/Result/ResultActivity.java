package com.example.david.githubrepositories.Result;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.example.david.githubrepositories.ListRepositories;
import com.example.david.githubrepositories.Model;
import com.example.david.githubrepositories.ModelImpl;
import com.example.david.githubrepositories.R;
import com.example.david.githubrepositories.Search.SearchActivity;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private List<Repositories> repositoriesList;
    private RecyclerView recyclerView;
    private String userName;
    private String ownerType;
    private Model model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        ownerType = intent.getStringExtra("ownerType");

        // TODO: 22.02.2017 Выполнение запроса к модели(загрузка из сервера/кеша)
        model = new ModelImpl(userName, ownerType);

        recyclerView = (RecyclerView) findViewById(R.id.rvRepositories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repositoriesList = model.requestToGitHub();


        ListRepositories adapter = new ListRepositories(repositoriesList);
        recyclerView.setAdapter(adapter);
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
                startActivity(intent);
                return true;
            case R.id.name_ascending:
                nameAscending();
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.name_descending:
                nameDescending();
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.date_ascending:
                dateAscending();
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.date_descending:
                dateDescending();
                recyclerView.getAdapter().notifyDataSetChanged();
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
            ListRepositories adapter = new ListRepositories(repositoriesList);
            recyclerView.swapAdapter(adapter, true);
        }
    }

    public void nameAscending() {
        if (!TextUtils.isEmpty(ownerType) && !TextUtils.isEmpty(userName)) {
            repositoriesList = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(userName), Repositories_Table.owner_type.is(ownerType))
                    .orderBy(Repositories_Table.name, true)
                    .queryList();
            ListRepositories adapter = new ListRepositories(repositoriesList);
            recyclerView.swapAdapter(adapter, true);
        }
    }

    public void dateAscending() {
        if (!TextUtils.isEmpty(ownerType) && !TextUtils.isEmpty(userName)) {
            repositoriesList = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(userName), Repositories_Table.owner_type.is(ownerType))
                    .orderBy(Repositories_Table.created_at, true)
                    .queryList();
            ListRepositories adapter = new ListRepositories(repositoriesList);
            recyclerView.swapAdapter(adapter, true);
        }
    }

    public void dateDescending() {
        if (!TextUtils.isEmpty(ownerType) && !TextUtils.isEmpty(userName)) {
            repositoriesList = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(userName), Repositories_Table.owner_type.is(ownerType))
                    .orderBy(Repositories_Table.created_at, false)
                    .queryList();
            ListRepositories adapter = new ListRepositories(repositoriesList);
            recyclerView.swapAdapter(adapter, true);
        }
    }
}
