package com.urke.saasbackendstarter.dto;

import lombok.*;

/**
 * Data Transfer Object for representing user file metadata.
 *
 * Contains essential information about uploaded files, such as file name, type, size, 
 * and upload timestamp, used for listing and download operations in the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFileDTO {
    private Long id;
    private String filename;
    private String contentType;
    private long size;
    private String uploadedAt;
}