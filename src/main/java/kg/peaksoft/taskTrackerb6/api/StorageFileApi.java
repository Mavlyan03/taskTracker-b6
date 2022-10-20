package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/file")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Storage Api", description = "Upload and delete files")
public class StorageFileApi {

    private final StorageService storageService;

    @Operation(summary = "Upload file", description = "Upload file to database")
    @PostMapping(path = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return storageService.upload(file);
    }

    @Operation(summary = "Delete file", description = "Delete file from database")
    @DeleteMapping("delete")
    public Map<String, String> deleteFile(@RequestParam String fileLink) {
        return storageService.delete(fileLink);
    }
}