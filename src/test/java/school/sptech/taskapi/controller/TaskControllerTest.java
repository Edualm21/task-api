package school.sptech.taskapi.controller;

import school.sptech.taskapi.dto.TaskRequest;
import school.sptech.taskapi.dto.TaskResponse;
import school.sptech.taskapi.exception.TaskNotFoundException;
import school.sptech.taskapi.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService service;

    private TaskController controller;

    @BeforeEach
    void setup() {
        controller = new TaskController(service);
    }

    @Test
    void shouldCreateTask() {
        TaskRequest request = new TaskRequest("New Task", "New Description", false);
        TaskResponse response = new TaskResponse(1L, "New Task", "New Description", false);
        when(service.create(any(TaskRequest.class))).thenReturn(response);

        ResponseEntity<TaskResponse> result = controller.create(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(service, times(1)).create(any(TaskRequest.class));
    }

    @Test
    void shouldListAllTasks() {
        Collection<TaskResponse> tasks = List.of(
                new TaskResponse(1L, "Task 1", "Description 1", false),
                new TaskResponse(2L, "Task 2", "Description 2", true)
        );
        when(service.findAll()).thenReturn(tasks);

        ResponseEntity<Collection<TaskResponse>> result = controller.findAll();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(service, times(1)).findAll();
    }

    @Test
    void shouldFindTaskById() {
        TaskResponse response = new TaskResponse(1L, "Task 1", "Description 1", false);
        when(service.findById(1L)).thenReturn(response);

        ResponseEntity<TaskResponse> result = controller.findById(1L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(service, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(service.findById(anyLong())).thenThrow(new TaskNotFoundException("Task not found"));

        assertThrows(TaskNotFoundException.class, () -> controller.findById(999L));
        verify(service, times(1)).findById(999L);
    }

    @Test
    void shouldUpdateTask() {
        TaskRequest request = new TaskRequest("Updated Task", "Updated Description", true);
        TaskResponse response = new TaskResponse(1L, "Updated Task", "Updated Description", true);
        when(service.update(anyLong(), any(TaskRequest.class))).thenReturn(response);

        ResponseEntity<TaskResponse> result = controller.update(1L, request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(service, times(1)).update(anyLong(), any(TaskRequest.class));
    }

    @Test
    void shouldDeleteTask() {
        doNothing().when(service).delete(anyLong());

        ResponseEntity<Void> result = controller.delete(1L);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(service, times(1)).delete(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentTask() {
        doThrow(new TaskNotFoundException("Task not found")).when(service).delete(anyLong());

        assertThrows(TaskNotFoundException.class, () -> controller.delete(999L));
        verify(service, times(1)).delete(999L);
    }
}

