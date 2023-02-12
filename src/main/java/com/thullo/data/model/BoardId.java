package com.thullo.data.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Setter
@Getter
@NoArgsConstructor
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
