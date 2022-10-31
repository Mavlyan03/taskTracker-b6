package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.LineService;
import kg.peaksoft.taskTrackerb6.dto.request.LineRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LineResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/line")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Line Api", description = "All endpoints of line")
public class LineApi {

    private final LineService lineService;

    @Operation(summary = "Create line", description = "Create new line")
    @PostMapping
    public LineResponse createLine(@RequestBody LineRequest lineRequest){
        return lineService.createLine(lineRequest);
    }

    @Operation(summary = "Update line", description = "Update title line")
    @PutMapping("update/{id}")
    public LineResponse updateLine(@PathVariable Long id,
                                   @RequestBody LineRequest lineRequest){
        return lineService.updateLine(id, lineRequest);
    }

    @Operation(summary = "Delete line", description = "Delete line by line id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteLine(@PathVariable Long id){
        return lineService.deleteLine(id);
    }

    @Operation(summary = "Add to archive", description = "Add to archive. Archive the line")
    @PutMapping("archive/{id}")
    public LineResponse addToArchive(@PathVariable Long id){
        return lineService.addToArchive(id);
    }

    @Operation(summary = "Send to board", description = "Send to board. Unarchive the line")
    @PutMapping("send-to-board/{id}")
    public LineResponse sendToBoard(@PathVariable Long id){
        return lineService.sendToBoard(id);
    }

    @Operation(summary = "Get all", description = "Get all lines by board id")
    @GetMapping("get-all/{id}")
    public List<LineResponse> findAllLinesByBoardId(@PathVariable Long id){
        return lineService.findAllLines(id);
    }

    @Operation(summary = "Find all", description = "Find all lines in archive")
    @GetMapping("get-all-in-archive")
    public List<LineResponse> findAllLinesByArchive(){
        return lineService.findAllLinesByArchive();
    }
}
