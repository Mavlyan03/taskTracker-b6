package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.service.BoardService;
import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ArchiveBoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.BoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.FavoritesResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/boards")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Board API", description = "The board CRUD methods (for manipulation)")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "Create board", description = "Create new board by workspaceId")
    @PostMapping
    public BoardResponse createBoard(@RequestBody BoardRequest boardRequest) {
        return boardService.createBoard(boardRequest);
    }

    @Operation(summary = "Board status",
            description = "This endpoint returns board status to favorite" +
                    "and not favorite for board further requests to the API")
    @PutMapping("/makeFavorite/{id}")
    public BoardResponse makeFavorite(@PathVariable Long id) {
        return boardService.makeFavorite(id);
    }

    @Operation(summary = "Get board", description = "Get board by board id")
    @GetMapping("{id}")
    public BoardResponse getById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @Operation(summary = "Delete board", description = "Delete board by board id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteById(@PathVariable Long id,
                                     @Valid Board board) {
        return boardService.deleteBoardById(id, board);
    }

    @Operation(summary = "Change background.", description = "Change background to a new one.")
    @PutMapping("/changeBackground/{id}")
    public BoardResponse changeBackground(@PathVariable Long id,
                                           @RequestBody BoardRequest boardRequest) {
        return boardService.changeBackground(id, boardRequest);
    }

    @Operation(summary = "Change title.", description = "Change title to a new one.")
    @PutMapping("/updateTitle/{id}")
    public BoardResponse updateTitle(@PathVariable Long id,
                                          @RequestBody BoardRequest boardRequest) {
        return boardService.updateTitle(id, boardRequest);
    }

    @Operation(summary = "Board status",
            description = "This endpoint returns board status to archive" +
                    "and not archive for board further requests to the API")
    @PutMapping("/sendToArchive/{id}")
    public BoardResponse sendToArchive(@PathVariable Long id) {
        return boardService.sendToArchive(id);
    }

    @Operation(summary = "All archive boards", description = "Get all archive boards")
    @GetMapping("archive")
    public List<ArchiveBoardResponse> getAllArchiveBoardsList() {
        return boardService.getAllArchiveBoardsList();
    }

    @Operation(summary = "Get all boards", description = "Get all boards by workspace id")
    @GetMapping("list/{id}")
    public List<BoardResponse> getAllBoardsByWorkspaceId(@PathVariable Long id) {
        return boardService.getAllBoardsByWorkspaceId(id);
    }
}