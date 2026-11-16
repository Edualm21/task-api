package school.sptech.taskapi.dto;

public record TaskResponse(Long id, String title, String description, boolean completed) {
}
