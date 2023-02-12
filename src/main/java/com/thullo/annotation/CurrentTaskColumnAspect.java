package com.thullo.annotation;


import com.thullo.data.model.Task;
import com.thullo.data.model.TaskColumn;
import com.thullo.service.BoardRefGenerator;
import com.thullo.service.TaskColumnService;
import com.thullo.web.payload.request.TaskRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class CurrentTaskColumnAspect {
    private final TaskColumnService taskColumnService;
    private final BoardRefGenerator boardRefGenerator;

    @Around("@annotation(CurrentTaskColumn)")
    public Object retrieveCurrentTaskColumn(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        TaskRequest taskRequest = findTaskRequest(args);
        if (taskRequest != null) {
            Long taskColumnId = taskRequest.getTaskColumnId();
            TaskColumn currentTaskColumn = taskColumnService.getCurrentTaskColumn(taskColumnId);
            taskRequest.setTask(createTask(taskRequest, currentTaskColumn));
        }
        return joinPoint.proceed(args);
    }

    private TaskRequest findTaskRequest(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof TaskRequest) {
                return (TaskRequest) arg;
            }
        }
        return null;
    }

    private Task createTask(TaskRequest taskRequest, TaskColumn currentTaskColumn) {
        Task task = new Task();
        task.setName(taskRequest.getName());
        task.setPosition((long) currentTaskColumn.getTasks().size());
        task.setBoardRef(boardRefGenerator.generateBoardRef(currentTaskColumn));
        task.setTaskColumn(currentTaskColumn);
        return task;
    }

}
