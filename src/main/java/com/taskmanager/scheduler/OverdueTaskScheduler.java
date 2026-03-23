package com.taskmanager.scheduler;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.enums.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class OverdueTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueTaskScheduler.class);
    private final TaskRepository taskRepository;

    public OverdueTaskScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void checkOverdueTasks() {
        LocalDate today = LocalDate.now();
        List<Task> overdueTasks = taskRepository.findOverdueTasks(today, TaskStatus.DONE);

        if (overdueTasks.isEmpty()) {
            log.info("No overdue tasks found on {}", today);
            return;
        }

        log.warn("Found {} overdue tasks:", overdueTasks.size());
        for (Task task : overdueTasks) {
            log.warn("  - Task [{}] '{}' was due on {} (Owner: {})",
                    task.getId(),
                    task.getTitle(),
                    task.getDueDate(),
                    task.getUser().getEmail());
        }
    }
}
