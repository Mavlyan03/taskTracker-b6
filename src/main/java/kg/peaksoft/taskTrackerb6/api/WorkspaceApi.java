package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.dto.request.WorkspaceRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.WorkspaceResponse;
import kg.peaksoft.taskTrackerb6.db.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("api/workspace")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Workspace Api", description = "All endpoints of workspace")
public class WorkspaceApi {

    private final WorkspaceService service;

    @Operation(summary = "Save workspace", description = "Save new workspace")
    @PostMapping
    public WorkspaceResponse save(@RequestBody WorkspaceRequest request,
                                    Authentication authentication) throws MessagingException {
        User user = (User) authentication.getPrincipal();
        return service.createWorkspace(request, user);
    }

    @Operation(summary = "Get workspace", description = "Get workspace by workspace id")
    @GetMapping("{id}")
    public WorkspaceResponse getById(@PathVariable Long id) {
        return service.getWorkspaceById(id);
    }

    @Operation(summary = "Delete workspace", description = "Delete workspace by workspace id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteById(@PathVariable Long id,
                                     Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return service.deleteWorkspaceById(id, user);
    }

    @Operation(summary = "All workspaces", description = "Get all workspaces")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping
    public List<WorkspaceResponse> getAll() {
        return service.getAllWorkspace();
    }

}
