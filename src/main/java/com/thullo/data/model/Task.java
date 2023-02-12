package com.thullo.data.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageUrl;

    private Long position;

    @Column(nullable = false, unique = true)
    private String boardRef;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "task_column_id")
    @JsonBackReference
    private TaskColumn taskColumn;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "task_contributor",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> contributors = new ArrayList<>();

    @ManyToMany
    private List<Label> labels = new ArrayList<>();
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public Task(String name) {
        this.name = name;

    }
}
