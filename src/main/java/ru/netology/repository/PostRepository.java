package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class PostRepository {
    private AtomicLong postId = new AtomicLong(0l);
    private ConcurrentHashMap<Long, Post> repository = new ConcurrentHashMap<>();

    public List<Post> all() {
        return repository.values().stream().toList();
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(repository.getOrDefault(id, null));

    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(postId.incrementAndGet());
        }
        if (repository.put(post.getId(), post) == null) {
            postId.set(Math.max(post.getId(), postId.get()));
        }
        return post;
    }

    public void removeById(long id) {
        try{
            repository.remove(id);
        } catch (NullPointerException e){
            throw new NotFoundException(String.format("No data found with id = %d", id));
        }
    }
}
