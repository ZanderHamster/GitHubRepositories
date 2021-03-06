package com.example.david.githubrepositories.Model;

import com.example.david.githubrepositories.Database.Repositories;
import com.example.david.githubrepositories.Database.Repositories_Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Model implements IModel {
    @Override
    public void loadToDataBase(List<Repositories> repositories, String username, String owner) {
        Date currentTime = Calendar.getInstance().getTime();
        for (int i = 0; i < repositories.size(); i++) {
            Repositories repo = new Repositories();

            repo.setName(repositories.get(i).getName());
            repo.setDescription(repositories.get(i).getDescription());
            repo.setCreated_at(repositories.get(i).getCreated_at());
            repo.setUpdated_at(repositories.get(i).getUpdated_at());
            repo.setLanguage(repositories.get(i).getLanguage());
            repo.setStargazers_count(repositories.get(i).getStargazers_count());
            repo.setRequest_time(currentTime);
            repo.setUser_name(username);
            repo.setOwner_type(owner);
            repo.save();
        }
    }

    @Override
    public List<Repositories> getRepositoriesList(String username, String owner) {
        return new Select()
                .from(Repositories.class)
                .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(owner))
                .queryList();
    }

    @Override
    public void ClearingHistory() {
        List<Repositories> uniqueUsers = new Select(Repositories_Table.user_name, Repositories_Table.owner_type, Repositories_Table.request_time)
                .distinct()
                .from(Repositories.class)
                .where()
                .queryList();

        if (uniqueUsers.size() > 2) {
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
}
