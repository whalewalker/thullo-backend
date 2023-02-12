package com.thullo.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "next_board_id")
@Getter
@Setter
public class BoardId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String boardRef;
    private Long nextId;
}
