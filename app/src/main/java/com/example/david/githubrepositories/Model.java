package com.example.david.githubrepositories;


import com.example.david.githubrepositories.Database.Repositories;

import java.util.List;

public interface Model {
    void loadToDataBase(List<Repositories> repositories, String username, String owner);

    List<Repositories> getRepositoriesList(String username, String owner);
}
