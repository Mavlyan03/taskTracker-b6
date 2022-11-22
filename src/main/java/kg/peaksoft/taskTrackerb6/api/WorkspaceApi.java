package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.model.Favorite;
import kg.peaksoft.taskTrackerb6.db.service.FavoriteService;
import kg.peaksoft.taskTrackerb6.db.service.WorkspaceService;
import kg.peaksoft.taskTrackerb6.dto.request.WorkspaceRequest;
import kg.peaksoft.taskTrackerb6.dto.response.FavoriteResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.WorkspaceResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("api/workspace")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Workspace API", description = "All endpoints of workspace")
public class WorkspaceApi {

    private final WorkspaceService service;
    private final FavoriteService favoriteService;

    @Operation(summary = "Create workspace", description = "Create new workspace")
    @PostMapping
    public WorkspaceResponse save(@RequestBody WorkspaceRequest request) throws MessagingException {
        return service.createWorkspace(request);
    }

    @Operation(summary = "Get workspace", description = "Get workspace by workspace id")
    @GetMapping("/{id}")
    public WorkspaceResponse getById(@PathVariable Long id) {
        return service.getWorkspaceById(id);
    }

    @Operation(summary = "Delete workspace", description = "Delete workspace by workspace id")
    @DeleteMapping("/{id}")
    public SimpleResponse deleteById(@PathVariable Long id) {
        return service.deleteWorkspaceById(id);
    }

    @Operation(summary = "Make favorite", description = "Make workspace favorite by workspace id")
    @PutMapping("/make-favorite/{id}")
    public WorkspaceResponse makeFavorite(@PathVariable Long id) {
        return service.makeFavorite(id);
    }

//    @Operation(summary = "Make not favorite", description = "Make workspace not favorite by id")
//    @PutMapping("make-not-favorite/{id}")
//    public WorkspaceResponse makeNotFavorite(@PathVariable Long id) {
//        return service.makeNotFavorite(id);
//    }

    @Operation(summary = "Get user workspaces", description = "Get all user workspaces")
    @GetMapping
    public List<WorkspaceResponse> getWorkspacesByUserId() {
        return service.getAllUserWorkspaces();
    }

    @Operation(summary = "Get all favorite workspaces and boards", description = "Get all user favorite workspaces and boards")
    @GetMapping("/favorites")
    public List<FavoriteResponse> getAllFavorite() {
        return favoriteService.getAllUserFavoriteWorkspacesAndBoards();
    }
}
