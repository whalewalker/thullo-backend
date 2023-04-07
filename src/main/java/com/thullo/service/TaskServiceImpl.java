package com.thullo.service;

import com.thullo.data.model.*;
import com.thullo.data.repository.AttachmentRepository;
import com.thullo.data.repository.BoardRepository;
import com.thullo.data.repository.TaskRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.StatusRequest;
import com.thullo.web.payload.request.TaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.thullo.util.Helper.*;
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
    private final BoardRepository boardRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    public Task createTask(String boardTag, String email, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException {
        Task task = mapper.map(taskRequest, Task.class);
        Board board = getBoard(boardTag);
        User createdBy = userRepository.findUserByEmail(email);

        task.setBoard(board);
        String taskStatus = formatStatus(taskRequest.getStatus());
        String status = isNullOrEmpty(taskStatus) ? "BACKLOG" : (taskStatus);

        long position = taskRepository.countByBoardAndStatus(board, taskStatus);

        task.setStatus(status);
        task.setPosition(position);

        String imageUrl = uploadTaskFile(taskRequest.getFile(), taskRequest.getRequestUrl());
        task.setImageUrl(imageUrl);
        task.setBoardRef(boardRefGenerator.generateBoardRef(board));
        task.setCreatedBy(createdBy);
        return taskRepository.save(task);
    }

    private Board getBoard(String boardTag) throws ResourceNotFoundException {
        return boardRepository.findByBoardTag(boardTag)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
    }

    private String uploadTaskFile(MultipartFile file, String requestUrl) throws BadRequestException, IOException {
        String imageUrl = null;
        if (file != null) {
            imageUrl = fileService.uploadFile(file, requestUrl);
        }
        return imageUrl;
    }

    @Override
    public Task moveTask(String boardRef, String status, Long position) throws ResourceNotFoundException {
        Task task = taskRepository.findByBoardRef(boardRef)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        String formattedStatus = formatStatus(status);
        List<Task> tasks = getAllByBoardAndStatus(task.getBoard(), formattedStatus);

        long index = Math.max(Math.min(position, tasks.size()), 0);
        boolean isSameStatus = task.getStatus().equals(formattedStatus);
        long currentIndex = task.getPosition();

        if (index > currentIndex && isSameStatus) {
            index--;
        }

        if (tasks.isEmpty()) {
            task.setPosition(0L);
        } else {
            for (Task t : tasks) {
                long tIndex = t.getPosition();
                if (tIndex >= index && tIndex < currentIndex) {
                    t.setPosition(tIndex + 1);
                } else if (tIndex <= index && tIndex > currentIndex) {
                    t.setPosition(tIndex - 1);
                }
            }
            task.setPosition(index);
        }
        task.setStatus(formattedStatus);
        return taskRepository.save(task);
    }


    @Override
    public Task editTask(String boardRef, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException {
        Task task = getTask(boardRef);
        mapper.map(taskRequest, task);
        String imageUrl = uploadTaskFile(taskRequest.getFile(), taskRequest.getRequestUrl());
        if (imageUrl != null) task.setImageUrl(imageUrl);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(String boardRef) throws ResourceNotFoundException {
        Task task = getTask(boardRef);
        if (task != null) {
            taskRepository.delete(task);
        }
    }

    @Override
    public List<Task> searchTask(String params) {
        return taskRepository.findByParams(params);
    }

    @Override
    public void addContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException {
        String title = "You have been added as a contributor on task: " + boardRef;
        String message = "You have been added as a contributor on task " + boardRef;

        Task task = getTask(boardRef);
        Set<User> existingContributors = task.getContributors();
        List<User> newContributors = userRepository.findAllByEmails(contributors);
        for (User contributor : newContributors) {
            if (!existingContributors.contains(contributor)) {
                existingContributors.add(contributor);
                roleService.addTaskRoleToUser(contributor, task);
                notificationService.sendNotificationToUser(contributor, message, title, NotificationType.ADDED_AS_CONTRIBUTOR);
            }
        }
    }

    @Override
    public void removeContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException {
        String title = "You have been removed as a contributor on task: " + boardRef;
        String message = "You have been removed as a contributor on task " + boardRef;

        Task task = getTask(boardRef);
        Set<User> existingContributors = task.getContributors();
        List<User> usersToRemove = userRepository.findAllByEmails(contributors);
        for (User contributor : usersToRemove) {
            existingContributors.remove(contributor);
            roleService.removeTaskRoleFromUser(contributor, task);
            notificationService.sendNotificationToUser(contributor, message, title, NotificationType.REMOVE_AS_CONTRIBUTOR);
        }
    }

    @Override
    public Task updateTaskImage(String boardRef, MultipartFile coverImage, String requestUrl) throws ResourceNotFoundException, IOException, BadRequestException {
        Task task = getTask(boardRef);

        String imageUrl = task.getImageUrl();
        if (imageUrl != null) {
            String fileId = extractFileIdFromUrl(imageUrl);
            fileService.deleteFile(fileId);
        }

        String newImageUrl = uploadCoverImage(coverImage, requestUrl);
        task.setImageUrl(newImageUrl);

        return taskRepository.save(task);
    }

    @Override
    public String getTaskImageUrl(String boardRef) throws ResourceNotFoundException {
        Task task = getTask(boardRef);
        return task.getImageUrl();
    }

    @Override
    public Attachment addAttachmentToTask(String boardRef, String url, MultipartFile file)
            throws ResourceNotFoundException, BadRequestException, IOException {
        Task task = getTask(boardRef);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileSize(calculateFileSize(file.getSize()));
        String fileUrl = fileService.uploadFile(file, url);
        attachment.setFileUrl(fileUrl);

        Attachment savedAttachment = attachmentRepository.save(attachment);
        task.getAttachments().add(savedAttachment);
        taskRepository.save(task);

        return savedAttachment;
    }

    @Override
    public void deleteAttachmentFromTask(String fileUrl, Long attachmentId) {
        fileService.deleteFile(extractFileIdFromUrl(fileUrl));
        attachmentRepository.findById(attachmentId).ifPresent(attachmentRepository::delete);
    }

    @Override
    public List<Task> editStatus(StatusRequest request) throws ResourceNotFoundException {
        Set<String> boardRefs = request.getBoardRef();
        String status = formatStatus(request.getStatus());

        List<Task> tasks = new ArrayList<>();
        for (String boardRef : boardRefs) {
            Task task = getTask(boardRef);
            task.setStatus(status);
            tasks.add(task);
        }
        return taskRepository.saveAll(tasks);
    }

    private List<Task> getAllByBoardAndStatus(Board board, String formattedStatus) {
        return taskRepository.findAllByBoardAndStatus(board, formattedStatus);
    }

    private String uploadCoverImage(MultipartFile coverImage, String requestUrl) throws IOException, BadRequestException {
        return fileService.uploadFile(coverImage, requestUrl);
    }

    public Task getTask(String boardRef) throws ResourceNotFoundException {
        return taskRepository.findByBoardRef(boardRef).orElseThrow(
                () -> new ResourceNotFoundException(format("Task with board ref %s not found", boardRef)));
    }
}