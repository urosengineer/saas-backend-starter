package com.urke.saasbackendstarter.mapper;

import com.urke.saasbackendstarter.domain.UserFile;
import com.urke.saasbackendstarter.dto.UserFileDTO;

public class UserFileMapper {
    public static UserFileDTO toDTO(UserFile entity) {
        return UserFileDTO.builder()
                .id(entity.getId())
                .filename(entity.getFilename())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .uploadedAt(entity.getUploadedAt().toString())
                .build();
    }
}