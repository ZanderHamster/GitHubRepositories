package com.example.david.githubrepositories.Model;


import com.example.david.githubrepositories.Database.Repositories;

import java.util.List;

public interface IModel {
    void loadToDataBase(List<Repositories> repositories, String username, String owner);

    List<Repositories> getRepositoriesList(String username, String owner);
}
