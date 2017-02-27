package com.example.david.githubrepositories;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModelImpl implements Model {
    private String username;
    private String type;

    public ModelImpl(String username, String type) {
        this.username = username;
        this.type = type;
    }

    @Override
    public List<Repositories> requestToGitHub() {
        if (TextUtils.isEmpty(username)) {
            return getRepositoriesList(username, type);
        } else {
            List<Repositories> currentRequest = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(type))
                    .queryList();
            if (currentRequest.isEmpty()) {
                Call<List<Repositories>> call = GitHubService
                        .Factory
                        .getInstance()
                        .getRepo(username, type);
                makeEnqueue(call, username, type);
                return getRepositoriesList(username, type);
            } else {
                Date requestTime = currentRequest.get(currentRequest.size() - 1).getRequest_time();
                Date currentTime = Calendar.getInstance().getTime();
                long timeDiff = currentTime.getTime() - requestTime.getTime();
                if ((timeDiff / 60000) > 5) {
                    Call<List<Repositories>> call = GitHubService
                            .Factory
                            .getInstance()
                            .getRepo(username, type);
                    makeEnqueue(call, username, type);
                    return getRepositoriesList(username, type);
                } else {
                    return getRepositoriesList(username, type);
                }
            }
        }

    }

    public void makeEnqueue(Call<List<Repositories>> call, final String username, final String owner) {
        call.enqueue(new Callback<List<Repositories>>() {
            @Override
            public void onResponse(Call<List<Repositories>> call, Response<List<Repositories>> response) {
                Date currentTime = Calendar.getInstance().getTime();
                for (int i = 0; i < response.body().size(); i++) {
                    Repositories repositories = new Repositories();

                    repositories.setName(response.body().get(i).getName());
                    repositories.setDescription(response.body().get(i).getDescription());
                    repositories.setCreated_at(response.body().get(i).getCreated_at());
                    repositories.setUpdated_at(response.body().get(i).getUpdated_at());
                    repositories.setLanguage(response.body().get(i).getLanguage());
                    repositories.setStargazers_count(response.body().get(i).getStargazers_count());
                    repositories.setRequest_time(currentTime);
                    repositories.setUser_name(username);
                    repositories.setOwner_type(owner);
                    repositories.save();
                }
                ClearingHistory();
            }

            @Override
            public void onFailure(Call<List<Repositories>> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });

    }

    public void ClearingHistory(){
        List<Repositories> uniqueUsers = new Select(Repositories_Table.user_name, Repositories_Table.owner_type, Repositories_Table.request_time)
                .distinct()
                .from(Repositories.class)
                .where()
                .queryList();

        if (uniqueUsers.size() >= 2) {
            Repositories oldestRequestTime = new Select(Method.min(Repositories_Table.request_time), Repositories_Table.request_time)
                    .from(Repositories.class)
                    .where()
                    .querySingle();
            List<Repositories> forDelete = new Select()
                    .from(Repositories.class)
                    .where(Repositories_Table.request_time.is(oldestRequestTime != null ? oldestRequestTime.getRequest_time() : null))
                    .queryList();
            for (int i = 0; i < forDelete.size(); i++) {
                forDelete.get(i).delete();
            }
        }
    }

    private List<Repositories> getRepositoriesList(String username, String type) {
        return new Select()
                .from(Repositories.class)
                .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(type))
                .queryList();
    }
}
