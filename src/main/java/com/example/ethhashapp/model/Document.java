package com.example.ethhashapp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Model representing a document.
 *
 * @author Yann39
 * @since 1.0.0
 */
@Getter
@Setter
public class Document {

    private String title;

    private String fileName;

    private byte[] fileData;

    private String hash256;

}
