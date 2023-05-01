package com.thullo.service;

import com.thullo.data.model.*;
import com.thullo.data.repository.AttachmentRepository;
import com.thullo.data.repository.TaskColumnRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thullo.util.Helper.calculateFileSize;
import static com.thullo.util.Helper.extractFileIdFromUrl;
import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final BoardRefGenerator boardRefGenerator;
    private final RoleServiceImpl roleService;
    private final TaskColumnRepository taskColumnRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    public TaskResponse createTask(String boardTag, String email, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException {
        Task task = mapper.map(taskRequest, Task.class);
        TaskColumn currentTaskColumn = findTaskColumn(taskRequest.getTaskColumnId());
        User createdBy = userRepository.findUserByEmail(email);
        String imageUrl = uploadTaskFile(taskRequest.getFile(), taskRequest.getRequestUrl());
        task.setImageUrl(imageUrl);
        task.setCreatedBy(createdBy);
        task.setPosition((long) currentTaskColumn.getTasks().size());
        task.setTaskColumn(currentTaskColumn);
        task.setBoardRef(boardRefGenerator.generateBoardRef(currentTaskColumn));
        Task savedTask = saveTask(task);
        return getTaskResponse(savedTask);
    }

    private TaskColumn findTaskColumn(Long columnId) throws ResourceNotFoundException {
        return taskColumnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Task column not found"));
    }

    private String uploadTaskFile(MultipartFile file, String requestUrl) throws BadRequestException, IOException {
        if (file == null) {
            return null;
        }
        return fileService.uploadFile(file, requestUrl);
    }

    @Override
    public TaskResponse moveTask(String boardRef, Long columnId, Long position) throws ResourceNotFoundException {
        Task task = getTaskByBoardRef(boardRef);
        List<Task> tasksInColumn = taskRepository.findByTaskColumnOrderByPositionAsc(columnId);
        if (tasksInColumn == null) {
            throw new ResourceNotFoundException("Task column not found!");
        }
        Long absolutePosition = Math.min(Math.max(position, 0L), (long) tasksInColumn.size() - 1);
        if (task.getTaskColumn().getId().equals(columnId) && task.getPosition() < absolutePosition) {
            absolutePosition--;
        }

        Long finalAbsolutePosition = absolutePosition;
        tasksInColumn.stream()
                .filter(t -> t.getPosition() >= finalAbsolutePosition)
                .forEach(t -> t.setPosition(t.getPosition() + 1));
        task.setPosition(absolutePosition);
        task.setTaskColumn(findTaskColumn(columnId));
        Task savedTask = saveTask(task);
        return getTaskResponse(savedTask);

    }

    private Task saveTask(Task task) {
        return taskRepository.save(task);
    }


    @Override
    public TaskResponse editTask(String boardRef, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException {
        Task task = getTaskByBoardRef(boardRef);
        mapper.map(taskRequest, task);

        String imageUrl = task.getImageUrl();
        String newImageUrl = null;

        if (taskRequest.getFile() != null) {
            if (imageUrl != null) {
                String fileId = extractFileIdFromUrl(imageUrl);
                fileService.deleteFile(fileId);
            }
            newImageUrl = uploadCoverImage(taskRequest.getFile(), taskRequest.getRequestUrl());
        }

        task.setImageUrl(newImageUrl);
        Task savedTask = saveTask(task);
        return getTaskResponse(savedTask);
    }


    @Override
    public void deleteTask(String boardRef) throws ResourceNotFoundException {
        Task task = getTaskByBoardRef(boardRef);
        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponse> searchTask(String params) {
        List<Task> tasks = taskRepository.findByParams(params);
        return tasks.stream()
                .map(this::getTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addContributor(String boardRef, String contributorEmail) throws ResourceNotFoundException, UserException {
        String title = "You have been added as a contributor on task: " + boardRef;
        String message = "You have been added as a contributor on task " + boardRef;

        Task task = getTaskByBoardRef(boardRef);
        Set<User> existingContributors = task.getContributors();
        User newContributor = findByEmail(contributorEmail);
        if (!existingContributors.contains(newContributor)) {
            existingContributors.add(newContributor);
            roleService.addTaskRoleToUser(newContributor, task);
            notificationService.sendNotificationToUser(newContributor, message, title, NotificationType.ADDED_AS_CONTRIBUTOR);

        }
    }

    @Override
    public void removeContributor(String boardRef, String contributorEmail) throws ResourceNotFoundException, UserException {
        String title = "You have been removed as a contributor on task: " + boardRef;
        String message = "You have been removed as a contributor on task " + boardRef;

        Task task = getTaskByBoardRef(boardRef);
        Set<User> existingContributors = task.getContributors();
        User userToRemove = findByEmail(contributorEmail);
        if (existingContributors.contains(userToRemove)) {
            existingContributors.remove(userToRemove);
            roleService.removeTaskRoleFromUser(userToRemove, task);
            notificationService.sendNotificationToUser(userToRemove, message, title, NotificationType.REMOVE_AS_CONTRIBUTOR);
        }
    }

    private User findByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(format("user not found with email %s", email)));
    }

    @Override
    public String getTaskImageUrl(String boardRef) throws ResourceNotFoundException {
        Task task = getTaskByBoardRef(boardRef);
        return task.getImageUrl();
    }

    @Override
    public Attachment addAttachmentToTask(String boardRef, String url, MultipartFile file)
            throws ResourceNotFoundException, BadRequestException, IOException {
        Task task = getTaskByBoardRef(boardRef);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileSize(calculateFileSize(file.getSize()));
        String fileUrl = fileService.uploadFile(file, url);
        attachment.setFileUrl(fileUrl);

        Attachment savedAttachment = attachmentRepository.save(attachment);
        task.getAttachments().add(savedAttachment);
        saveTask(task);

        return savedAttachment;
    }

    @Override
    public void deleteAttachmentFromTask(String fileUrl, Long attachmentId) {
        fileService.deleteFile(extractFileIdFromUrl(fileUrl));
        attachmentRepository.findById(attachmentId).ifPresent(attachmentRepository::delete);
    }

    private String uploadCoverImage(MultipartFile coverImage, String requestUrl) throws IOException, BadRequestException {
        return fileService.uploadFile(coverImage, requestUrl);
    }

    public TaskResponse getTask(String boardRef) throws ResourceNotFoundException {
        Task task = getTaskByBoardRef(boardRef);
        return getTaskResponse(task);
    }

    private Task getTaskByBoardRef(String boardRef) throws ResourceNotFoundException {
        return taskRepository.findByBoardRef(boardRef).orElseThrow(
                () -> new ResourceNotFoundException(format("Task with board ref '%s' not found", boardRef)));
    }

    private TaskResponse getTaskResponse(Task task) {
        return mapper.map(task, TaskResponse.class);
    }
}