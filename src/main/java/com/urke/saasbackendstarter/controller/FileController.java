package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.domain.UserFile;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.service.FileUploadService;
import com.urke.saasbackendstarter.service.UserService;
import com.urke.saasbackendstarter.dto.UserFileDTO;
import com.urke.saasbackendstarter.mapper.UserFileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

/**
 * REST controller for file upload, download, and listing for users.
 */
@Tag(
    name = "File Management",
    description = "Endpoints for user file upload, download, and listing."
)
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;
    private final UserService userService;
    private final MessageSource messageSource;
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Operation(
    	    summary = "Upload a file",
    	    description = "Upload a file and associate it with the authenticated user.",
    	    security = @SecurityRequirement(name = "bearerAuth"),
    	    responses = {
    	        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
    	        @ApiResponse(responseCode = "401", description = "Unauthorized"),
    	        @ApiResponse(responseCode = "400", description = "Invalid file or request")
    	    }
    	)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            Principal principal,
            Locale locale
    ) {
        log.info("File upload requested by user: {}", principal.getName());
        log.debug("File name: {}, size: {}", file.getOriginalFilename(), file.getSize());

        User user = userService.findByEmailWithOrganization(principal.getName())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", principal.getName());
                    return new com.urke.saasbackendstarter.exception.UserNotFoundException(
                        messageSource.getMessage("user.notfound", null, locale));
                });

        if (user.getOrganization() == null) {
            log.error("User {} has no organization set!", user.getEmail());
            throw new com.urke.saasbackendstarter.exception.FileUploadException("User has no organization set.");
        }

        try {
            fileUploadService.uploadFile(user, file);
            String msg = messageSource.getMessage("file.upload.success", null, locale);
            log.info("File uploaded successfully for user: {}", user.getEmail());
            return ResponseEntity.ok().body(msg);
        } catch (Exception e) {
            log.error("Error during file upload for user {}: {}", user.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
        summary = "Download a file",
        description = "Download a file by its ID, only if owned by the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth"),
        parameters = {
            @Parameter(name = "fileId", description = "ID of the file to download", required = true)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "File not found or not owned by user")
        }
    )
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long fileId,
            Principal principal,
            Locale locale
    ) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new com.urke.saasbackendstarter.exception.UserNotFoundException(
                    messageSource.getMessage("user.notfound", null, locale)));
        byte[] data = fileUploadService.downloadFile(fileId, user);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @Operation(
        summary = "List my files",
        description = "List all files uploaded by the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "List of files returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @GetMapping("/my")
    public ResponseEntity<List<UserFileDTO>> listMyFiles(
            Principal principal,
            Locale locale
    ) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new com.urke.saasbackendstarter.exception.UserNotFoundException(
                        messageSource.getMessage("user.notfound", null, locale)));
        List<UserFile> files = fileUploadService.listFiles(user);
        List<UserFileDTO> result = files.stream()
                .map(UserFileMapper::toDTO)
                .toList();
        return ResponseEntity.ok(result);
    }
}