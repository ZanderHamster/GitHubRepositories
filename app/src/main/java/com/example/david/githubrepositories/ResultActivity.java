package com.example.david.githubrepositories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResultActivity extends AppCompatActivity{
    private List<Repositories> repositoriesList;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String ownerType= intent.getStringExtra("ownerType");

        recyclerView = (RecyclerView) findViewById(R.id.rvRepositories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repositoriesList = new Select().from(Repositories.class).where(Repositories_Table.user_name.is(userName),Repositories_Table.owner_type.is(ownerType)).queryList();
        ListRepositories adapter = new ListRepositories(repositoriesList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.name_ascending:
                Collections.sort(repositoriesList,ResultActivity.nameAscending);
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.name_descending:
                Collections.sort(repositoriesList,ResultActivity.nameDescending);
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.date_ascending:
                Collections.sort(repositoriesList,ResultActivity.dateAscending);
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.date_descending:
                Collections.sort(repositoriesList,ResultActivity.dateDescending);
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
        }
        return true;
    }

    public static Comparator<Repositories> dateDescending = new Comparator<Repositories>() {
        @Override
        public int compare(Repositories repositories, Repositories t1) {
            return repositories.getCreated_at().compareTo(t1.getCreated_at());
        }
    };

    public static Comparator<Repositories> dateAscending = new Comparator<Repositories>() {
        @Override
        public int compare(Repositories repositories, Repositories t1) {
            return t1.getCreated_at().compareTo(repositories.getCreated_at());
        }
    };
    public static Comparator<Repositories> nameAscending = new Comparator<Repositories>() {
        @Override
        public int compare(Repositories repositories, Repositories t1) {
            return repositories.getName().compareTo(t1.getName());
        }
    };
    public static Comparator<Repositories> nameDescending = new Comparator<Repositories>() {
        @Override
        public int compare(Repositories repositories, Repositories t1) {
            return t1.getName().compareTo(repositories.getName());
        }
    };
}
