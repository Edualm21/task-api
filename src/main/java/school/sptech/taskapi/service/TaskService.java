package school.sptech.taskapi.service;

import school.sptech.taskapi.dto.TaskRequest;
import school.sptech.taskapi.dto.TaskResponse;
import school.sptech.taskapi.entity.Task;
import school.sptech.taskapi.exception.TaskNotFoundException;
import school.sptech.taskapi.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(TaskRequest request) {
        Task task = new Task(null, request.getTitle(), request.getDescription(), request.isCompleted());
        Task saved = repository.save(task);
        return toResponse(saved);
    }

    public Collection<TaskResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse findById(Long id) {
        Task task = repository.findById(id);
        if (task == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        return toResponse(task);
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = repository.findById(id);
        if (task == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        Task updated = repository.save(task);
        return toResponse(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }
}
