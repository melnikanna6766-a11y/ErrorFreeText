package com.github.melnikanna6766a11y.errorfreetext.exceptions;

import com.github.melnikanna6766a11y.errorfreetext.dto.ErrorResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Log4j2
@AllArgsConstructor
public class ServerExceptionHandler {
    private TaskService taskService;

    public void handleSpellerServerErrorException(String massage, Task task) {
        log.error(massage);
        task.setError(new ErrorResponse(
                massage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now(),
                null));
        task.setCompletionDate(LocalDate.now());
        taskService.saveStatusFor(Status.error, task);
    }

    @Transactional
    public void handleCounterOverflowException() {
        log.error("The number of requests per day or the number of characters has exceeded the limit");
        taskService.findUncompletedTasks().forEach(task -> {
            if (task.getSpellerResponses() != null) {
                task.setCompletionDate(LocalDate.now());
                taskService.saveStatusFor(Status.incompleted, task);
            } else {
                taskService.saveStatusFor(Status.created, task);
            }
        });
    }
}
