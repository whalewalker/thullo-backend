package com.thullo.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "next_board_id")
public class BoardId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String boardTag;
    private  Long nextId;

    public BoardId(String boardTag, Long nextId) {
        this.boardTag = boardTag;
        this.nextId = nextId;
    }
}
