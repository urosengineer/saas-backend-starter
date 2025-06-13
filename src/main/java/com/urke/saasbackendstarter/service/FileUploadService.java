package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.domain.UserFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Service for user file upload, download, and listing.
 */
public interface FileUploadService {
    UserFile uploadFile(User user, MultipartFile file);
    byte[] downloadFile(Long fileId, User user);
    List<UserFile> listFiles(User user);
}