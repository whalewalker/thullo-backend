//package com.thullo.service;
//
//import com.thullo.data.model.*;
//import com.thullo.data.repository.BoardRepository;
//import com.thullo.data.repository.TaskRepository;
//import com.thullo.data.repository.UserRepository;
//import com.thullo.web.exception.BadRequestException;
//import com.thullo.web.exception.ResourceNotFoundException;
//import com.thullo.web.payload.request.TaskRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Set;
//
//import static java.lang.String.format;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class TaskServiceImpl implements TaskService {
//    private final TaskRepository taskRepository;
//    private final ModelMapper mapper;
//    private final FileService fileService;
//    private final UserRepository userRepository;
//    private final NotificationService notificationService;
//    private final BoardRefGenerator boardRefGenerator;
//
//    private final BoardRepository boardRepository;
//
//    @Override
//    public Task createTask(String boardTag, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException {
//        Task task = mapper.map(taskRequest, Task.class);
//        Board board = getBoard(boardTag);
//        task.setBoardRef(boardRefGenerator.generateBoardRef(board));
//        task.setBoard(board);
//        Status status = Status.getStatus(taskRequest.getStatus().toLowerCase());
//        long position = taskRepository.countByBoardAndStatus(board, status);
//
//        task.setStatus(status);
//        task.setPosition(position);
//
//        String imageUrl = uploadTaskFile(taskRequest.getFile(), taskRequest.getRequestUrl());
//        task.setImageUrl(imageUrl);
//        return taskRepository.save(task);
//    }
//
//    private Board getBoard(String boardTag) throws ResourceNotFoundException {
//        return boardRepository.findByBoardTag(boardTag)
//                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
//    }
//
//    private String uploadTaskFile(MultipartFile file, String requestUrl) throws BadRequestException, IOException {
//        String imageUrl = null;
//        if (file != null) {
//            imageUrl = fileService.uploadFile(file, requestUrl);
//        }
//        return imageUrl;
//    }
//
//    @Override
//    public Task moveTask(Long taskId, Long newColumnId, Long index) throws ResourceNotFoundException {
//        Long absoluteIndex = Math.abs(index);
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found !"));
//
//        List<Task> tasksInColumn = taskRepository.findByTaskColumnOrderByPositionAsc(newColumnId).orElseThrow(
//                () -> new ResourceNotFoundException("Task column not found !"));
//
//        if (absoluteIndex > tasksInColumn.size())
//            absoluteIndex = (long) tasksInColumn.size();
//
////        if (task.getTaskColumn().getId().equals(newColumnId) && task.getPosition() < absoluteIndex) {
////            absoluteIndex--;
////        }
//
//
//        Long finalIndex = absoluteIndex;
//        tasksInColumn.stream()
//                .filter(t -> t.getPosition() >= finalIndex)
//                .forEach(t -> t.setPosition(t.getPosition() + 1));
//
//        task.setPosition(absoluteIndex);
//        return taskRepository.save(task);
//    }
//
//    @Override
//    @CachePut(value = "tasks", key = "#taskId")
//    public Task editTask(Long taskId, TaskRequest taskRequest) throws BadRequestException, IOException, ResourceNotFoundException {
//        Task task = getTask(taskId);
//        mapper.map(taskRequest, task);
//        String imageUrl = uploadTaskFile(taskRequest.getFile(), taskRequest.getRequestUrl());
//        if (imageUrl != null) task.setImageUrl(imageUrl);
//        return task;
//    }
//
//    private Task getTaskInternal(Long taskId) {
//        return taskRepository.findById(taskId).orElse(null);
//    }
//
//
//    public Task getTask(Long taskId) throws ResourceNotFoundException {
//        Task task = getTaskInternal(taskId);
//        if (task == null) throw new ResourceNotFoundException("Task not found !");
//        return task;
//    }
//
//    @Override
//    @CacheEvict(value = "tasks", key = "#taskId")
//    public void deleteTask(Long taskId) {
//        Task task = getTaskInternal(taskId);
//        if (task != null) {
//            taskRepository.delete(task);
//        }
//    }
//
//    @Override
//    public List<Task> findTaskContainingNameOrBoardId(String name, String boardRef) {
//        return taskRepository.findByNameContainingOrBoardRef(name, boardRef);
//    }
//
//    @Override
//    public void addContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException {
//        String title = "You have been added as a contributor on task: " + boardRef;
//        String message = "You have been added as a contributor on task " + boardRef;
//
//        Task task = getTask(boardRef);
//        Set<User> existingContributors = task.getContributors();
//        List<User> newContributors = userRepository.findAllByEmails(contributors);
//        for (User contributor : newContributors) {
//            if (!existingContributors.contains(contributor)) {
//                existingContributors.add(contributor);
//                notificationService.sendNotificationToUser(contributor, message, title, NotificationType.ADDED_AS_CONTRIBUTOR);
//            }
//        }
//    }
//
//    @Override
//    public void removeContributors(String boardRef, Set<String> contributors) throws ResourceNotFoundException {
//        String title = "You have been removed as a contributor on task: " + boardRef;
//        String message = "You have been removed as a contributor on task " + boardRef;
//
//        Task task = getTask(boardRef);
//        Set<User> existingContributors = task.getContributors();
//        List<User> usersToRemove = userRepository.findAllByEmails(contributors);
//        for (User contributor : usersToRemove) {
//            existingContributors.remove(contributor);
//            notificationService.sendNotificationToUser(contributor, message, title, NotificationType.REMOVE_AS_CONTRIBUTOR);
//        }
//    }
//
//    @Override
//    public Task updateTaskImage(String boardRef, MultipartFile coverImage, String requestUrl) throws ResourceNotFoundException, IOException, BadRequestException {
//        Task task = getTask(boardRef);
//
//        String imageUrl = task.getImageUrl();
//        if (imageUrl != null) {
//            String fileId = extractFileIdFromUrl(imageUrl);
//            fileService.deleteFile(fileId);
//        }
//
//        String newImageUrl = uploadCoverImage(coverImage, requestUrl);
//        task.setImageUrl(newImageUrl);
//
//        return taskRepository.save(task);
//    }
//
//    @Override
//    public String getTaskImageUrl(String boardRef) throws ResourceNotFoundException {
//        Task task = getTask(boardRef);
//        return task.getImageUrl();
//    }
//
//    private String extractFileIdFromUrl(String imageUrl) {
//        return imageUrl.substring(imageUrl.indexOf("files/") + 6);
//    }
//
//    private String uploadCoverImage(MultipartFile coverImage, String requestUrl) throws IOException, BadRequestException {
//        return fileService.uploadFile(coverImage, requestUrl);
//    }
//
//
//    public Task getTask(String boardRef) throws ResourceNotFoundException {
//        return taskRepository.findByBoardRef(boardRef).orElseThrow(
//                () -> new ResourceNotFoundException(format("Task with board ref %s not found", boardRef)));
//    }
//
//}
