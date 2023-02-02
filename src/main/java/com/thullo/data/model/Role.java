package com.thullo.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long roleId;

  @NaturalId
  @Column(length = 60)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private Collection<User> Users;

  @CreationTimestamp
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;

  public Role(String name) {
    this.name = name;
  }
}
