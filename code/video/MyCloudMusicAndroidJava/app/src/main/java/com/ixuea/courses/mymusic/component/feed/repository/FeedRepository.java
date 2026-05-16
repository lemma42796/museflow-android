package com.ixuea.courses.mymusic.component.feed.repository;

import com.ixuea.courses.mymusic.component.feed.model.Feed;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;

import io.reactivex.rxjava3.core.Observable;

/**
 * Repository for feed list refreshes.
 */
public class FeedRepository {
    private static FeedRepository instance;
    private final DefaultRepository repository;

    private FeedRepository(DefaultRepository repository) {
        this.repository = repository;
    }

    public synchronized static FeedRepository getInstance() {
        if (instance == null) {
            instance = new FeedRepository(DefaultRepository.getInstance());
        }
        return instance;
    }

    public Observable<ListResponse<Feed>> feeds(String userId) {
        return repository.feeds(userId);
    }
}
