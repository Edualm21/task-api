package school.sptech.taskapi.service;

import school.sptech.taskapi.dto.TaskRequest;
import school.sptech.taskapi.dto.TaskResponse;
import school.sptech.taskapi.exception.TaskNotFoundException;
import school.sptech.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.taskapi.entity.Task;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(repository);
    }

    @Test
    void shouldCreateTask() {
        TaskRequest request = new TaskRequest("Test Task", "Test Description", false);
        Task entity = new Task(1L, "Test Task", "Test Description", false);
        when(repository.save(any(Task.class))).thenReturn(entity);

        TaskResponse response = service.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test Task", response.title());
        assertEquals("Test Description", response.description());
        assertFalse(response.completed());
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldFindAllTasks() {
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", "Description 1", false),
                new Task(2L, "Task 2", "Description 2", true)
        );
        when(repository.findAll()).thenReturn(tasks);

        Collection<TaskResponse> responses = service.findAll();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldFindTaskById() {
        Task task = new Task(1L, "Test Task", "Test Description", false);
        when(repository.findById(1L)).thenReturn(task);

        TaskResponse response = service.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test Task", response.title());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(repository.findById(anyLong())).thenReturn(null);

        assertThrows(TaskNotFoundException.class, () -> service.findById(999L));
        verify(repository, times(1)).findById(999L);
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task(1L, "Old Title", "Old Description", false);
        TaskRequest request = new TaskRequest("New Title", "New Description", true);
        Task updated = new Task(1L, "New Title", "New Description", true);

        when(repository.findById(1L)).thenReturn(task);
        when(repository.save(any(Task.class))).thenReturn(updated);

        TaskResponse response = service.update(1L, request);

        assertNotNull(response);
        assertEquals("New Title", response.title());
        assertEquals("New Description", response.description());
        assertTrue(response.completed());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentTask() {
        TaskRequest request = new TaskRequest("New Title", "New Description", false);
        when(repository.findById(anyLong())).thenReturn(null);

        assertThrows(TaskNotFoundException.class, () -> service.update(999L, request));
        verify(repository, times(1)).findById(999L);
    }

    @Test
    void shouldDeleteTask() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentTask() {
        when(repository.existsById(anyLong())).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> service.delete(999L));
        verify(repository, times(1)).existsById(999L);
    }
}
