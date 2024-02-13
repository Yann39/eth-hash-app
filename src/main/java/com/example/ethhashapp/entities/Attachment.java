package com.example.ethhashapp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Yann39
 * @since 1.0.0
 */
@Getter
@Setter
@Entity
@Table(name = "attachment")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Lob
    private byte[] file;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

}