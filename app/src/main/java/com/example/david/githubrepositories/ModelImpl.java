package com.example.david.githubrepositories;

import android.os.AsyncTask;
import android.text.TextUtils;

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
                new ParseRepositories().execute(username, type);
                return getRepositoriesList(username, type);
            } else {
                Date requestTime = currentRequest.get(currentRequest.size() - 1).getRequest_time();
                Date currentTime = Calendar.getInstance().getTime();
                long timeDiff = currentTime.getTime() - requestTime.getTime();
                if ((timeDiff / 60000) > 5) {
                    new ParseRepositories().execute(username, type);
                    return getRepositoriesList(username, type);
                } else {
                    return getRepositoriesList(username, type);
                }
            }
        }

    }

    private List<Repositories> getRepositoriesList(String username, String type) {
        return new Select()
                .from(Repositories.class)
                .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(type))
                .queryList();
    }

    private class ParseRepositories extends AsyncTask<String, Void, String> {
        private String resultJson = "";
        private String userId;
        private String userName;
        private String ownerType;

        @Override
        protected String doInBackground(String... urls) {
            try {
                userName = urls[0];
                ownerType = urls[1];

                URL url = new URL("https://api.github.com/users/" + userName + "/repos?type=" + ownerType);
                URL id = new URL("https://api.github.com/users/" + userName);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

                urlConnection = (HttpURLConnection) id.openConnection();
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                buffer = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String lineId;
                while ((lineId = reader.readLine()) != null) {
                    buffer.append(lineId);
                }
                JSONObject object = new JSONObject(buffer.toString());
                userId = object.getString("id");

                urlConnection.disconnect();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            JSONArray dataJsonObj;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            try {
                dataJsonObj = new JSONArray(strJson);

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

                Date currentTime = Calendar.getInstance().getTime();
                if (!dataJsonObj.getJSONObject(0).getString("name").isEmpty()) {
                    for (int i = 0; i < dataJsonObj.length(); i++) {
                        Repositories repositories = new Repositories();


                        String owner = dataJsonObj.getJSONObject(i).getJSONObject("owner").getString("id");
                        if (owner.equals(userId))
                            repositories.setName(dataJsonObj.getJSONObject(i).getString("name"));
                        else
                            repositories.setName(dataJsonObj.getJSONObject(i).getString("full_name"));
                        repositories.setDescription(dataJsonObj.getJSONObject(i).getString("description"));
                        repositories.setCreated_at(format.parse(dataJsonObj.getJSONObject(i).getString("created_at")));
                        repositories.setUpdated_at(format.parse(dataJsonObj.getJSONObject(i).getString("updated_at")));
                        repositories.setLanguage(dataJsonObj.getJSONObject(i).getString("language"));
                        repositories.setStargazers_count(Integer.valueOf(dataJsonObj.getJSONObject(i).getString("stargazers_count")));
                        repositories.setRequest_time(currentTime);
                        repositories.setUser_name(userName);
                        repositories.setOwner_type(ownerType);
                        repositories.save();
                    }
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
