//package com.example.david.githubrepositories;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.example.david.githubrepositories.Database.Repositories;
//import com.example.david.githubrepositories.Database.Repositories_Table;
//import com.raizlabs.android.dbflow.sql.language.Method;
//import com.raizlabs.android.dbflow.sql.language.Select;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//import io.reactivex.Observable;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//
//public class ModelImpl implements Model {
//    private String username;
//    private String owner;
//
//    public ModelImpl(String username, String owner) {
//        this.username = username;
//        this.owner = owner;
//    }
//
//    @Override
//    public List<Repositories> requestToGitHub() {
//        if (TextUtils.isEmpty(username)) {
//            return getRepositoriesList(username, owner);
//        } else {
//            List<Repositories> currentRequest = new Select()
//                    .from(Repositories.class)
//                    .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(owner))
//                    .queryList();
//            if (currentRequest.isEmpty()) {
//                makeRequest();
//                return getRepositoriesList(username, owner);
//            } else {
//                Date requestTime = currentRequest.get(currentRequest.size() - 1).getRequest_time();
//                Date currentTime = Calendar.getInstance().getTime();
//                long timeDiff = currentTime.getTime() - requestTime.getTime();
//                if ((timeDiff / 60000) > 5) {
//                    makeRequest();
//                    return getRepositoriesList(username, owner);
//                } else {
//                    return getRepositoriesList(username, owner);
//                }
//            }
//        }
//
//    }
//
//    public void makeRequest() {
//        GitHubServiceImpl gitHubService = new GitHubServiceImpl();
//        Observable<List<Repositories>> data = gitHubService.getRepo(username, owner)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread());
//        data.subscribe(new Observer<List<Repositories>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.d("LOG", "onSubscribe");
//            }
//
//            @Override
//            public void onNext(List<Repositories> repositories) {
//                Log.d("LOG", "onNext");
//                Date currentTime = Calendar.getInstance().getTime();
//                for (int i = 0; i < repositories.size(); i++) {
//                    Log.d("LOG", repositories.get(i).getName());
//                    Repositories repo = new Repositories();
//
//                    repo.setName(repositories.get(i).getName());
//                    repo.setDescription(repositories.get(i).getDescription());
//                    repo.setCreated_at(repositories.get(i).getCreated_at());
//                    repo.setUpdated_at(repositories.get(i).getUpdated_at());
//                    repo.setLanguage(repositories.get(i).getLanguage());
//                    repo.setStargazers_count(repositories.get(i).getStargazers_count());
//                    repo.setRequest_time(currentTime);
//                    repo.setUser_name(username);
//                    repo.setOwner_type(owner);
//                    repo.save();
//                }
//                ClearingHistory();
//            }
//
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d("LOG", e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d("LOG", "Complete");
//            }
//        });
//    }
//
//
//    public void ClearingHistory() {
//        List<Repositories> uniqueUsers = new Select(Repositories_Table.user_name, Repositories_Table.owner_type, Repositories_Table.request_time)
//                .distinct()
//                .from(Repositories.class)
//                .where()
//                .queryList();
//
//        if (uniqueUsers.size() > 2) {
//            Repositories oldestRequestTime = new Select(Method.min(Repositories_Table.request_time), Repositories_Table.request_time)
//                    .from(Repositories.class)
//                    .where()
//                    .querySingle();
//            List<Repositories> forDelete = new Select()
//                    .from(Repositories.class)
//                    .where(Repositories_Table.request_time.is(oldestRequestTime != null ? oldestRequestTime.getRequest_time() : null))
//                    .queryList();
//            for (int i = 0; i < forDelete.size(); i++) {
//                forDelete.get(i).delete();
//            }
//        }
//    }
//
//    private List<Repositories> getRepositoriesList(String username, String owner) {
//        return new Select()
//                .from(Repositories.class)
//                .where(Repositories_Table.user_name.is(username), Repositories_Table.owner_type.is(owner))
//                .queryList();
//    }
//}
