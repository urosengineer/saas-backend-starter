package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.domain.UserFile;
import com.urke.saasbackendstarter.exception.FileNotFoundOrForbiddenException;
import com.urke.saasbackendstarter.exception.FileUploadException;
import com.urke.saasbackendstarter.repository.UserFileRepository;
import com.urke.saasbackendstarter.security.CurrentUserProvider;
import com.urke.saasbackendstarter.service.FileUploadService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the file upload/download service.
 */
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final UserFileRepository userFileRepository;
    private final CurrentUserProvider currentUserProvider;
    private final MessageSource messageSource;
    
    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Value("${app.upload.dir:uploads}")
    private String root;

    @Override
    public UserFile uploadFile(User user, MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(root));
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destination = Paths.get(root).resolve(filename);
            Files.copy(file.getInputStream(), destination);

            UserFile userFile = UserFile.builder()
                    .filename(filename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .user(user)
                    .organization(user.getOrganization())
                    .uploadedAt(LocalDateTime.now())
                    .build();
            return userFileRepository.save(userFile);
        } catch (IOException e) {
            log.error("File upload failed for user {}: {} - cause: {}", 
                user.getEmail(), file.getOriginalFilename(), e.getMessage(), e);
            throw new FileUploadException(
                messageSource.getMessage("file.upload.error", null, LocaleContextHolder.getLocale()), e
            );
        }
    }

    @Override
    public byte[] downloadFile(Long fileId, User user) {
        UserFile userFile = userFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundOrForbiddenException(
                    messageSource.getMessage("file.notfound", null, LocaleContextHolder.getLocale())
                ));
        if (!userFile.getUser().getId().equals(user.getId()) ||
        	    !userFile.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new FileNotFoundOrForbiddenException(
                messageSource.getMessage("file.notfound", null, LocaleContextHolder.getLocale())
            );
        }
        try {
            return Files.readAllBytes(Paths.get(root).resolve(userFile.getFilename()));
        } catch (IOException e) {
            throw new FileUploadException(
                messageSource.getMessage("file.upload.error", null, LocaleContextHolder.getLocale()), e
            );
        }
    }

    @Override
    public List<UserFile> listFiles(User user) {
        return userFileRepository.findByUserAndOrganization(user, user.getOrganization());
    }
}