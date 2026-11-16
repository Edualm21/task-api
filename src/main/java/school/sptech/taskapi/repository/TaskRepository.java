package school.sptech.taskapi.repository;

import school.sptech.taskapi.entity.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {
    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.incrementAndGet());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Collection<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Task findById(Long id) {
        return tasks.get(id);
    }

    public void deleteById(Long id) {
        tasks.remove(id);
    }

    public boolean existsById(Long id) {
        return tasks.containsKey(id);
    }
}
