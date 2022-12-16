package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.service.BoardService;
import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ArchiveResponse;
import kg.peaksoft.taskTrackerb6.dto.response.BoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/boards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Board API", description = "All board endpoints for Admin")
public class BoardApi {

    private final BoardService boardService;

    @Operation(summary = "Create board", description = "Create new board by workspaceId")
    @PostMapping
    public BoardResponse createBoard(@RequestBody BoardRequest boardRequest) {
        return boardService.createBoard(boardRequest);
    }

    @Operation(summary = "Board status",
            description = "This endpoint returns board status to favorite")
    @PutMapping("/make-favorite/{id}")
    public BoardResponse makeFavorite(@PathVariable Long id) {
        return boardService.makeFavorite(id);
    }

    @Operation(summary = "Get board", description = "Get board by id")
    @GetMapping("/{id}")
    public BoardResponse getById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @Operation(summary = "Delete board", description = "Delete board by id")
    @DeleteMapping("/{id}")
    public SimpleResponse deleteById(@PathVariable Long id,
                                     @Valid Board board) {
        return boardService.deleteBoardById(id, board);
    }

    @Operation(summary = "Change background", description = "Change background to a new one")
    @PutMapping("/change-background/{id}")
    public BoardResponse changeBackground(@PathVariable Long id,
                                          @RequestBody BoardRequest boardRequest) {
        return boardService.changeBackground(id, boardRequest);
    }

    @Operation(summary = "Change title", description = "Change title to a new one")
    @PutMapping("/title")
    public BoardResponse updateTitle(@RequestBody UpdateRequest request) {
        return boardService.updateTitle(request);
    }

    @Operation(summary = "Get all boards", description = "Get all boards by workspace id")
    @GetMapping("/list/{id}")
    public List<BoardResponse> getAllBoardsByWorkspaceId(@PathVariable Long id) {
        return boardService.getAllBoardsByWorkspaceId(id);
    }

    @Operation(summary = "Get all in archive", description = "Get all in archive by board id")
    @GetMapping("/archive/{id}")
    public ArchiveResponse getInArchiveByBoardId(@PathVariable Long id) {
        return boardService.getAllArchivedCardsByBoardId(id);
    }
}