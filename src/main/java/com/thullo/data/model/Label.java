package com.thullo.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
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
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private String colorCode;

    private String backgroundCode;

    @ManyToMany(mappedBy="labels")
    private List<Task> tasks = new ArrayList<>();

    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
