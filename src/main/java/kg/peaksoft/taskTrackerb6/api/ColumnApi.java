package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.ColumnService;
import kg.peaksoft.taskTrackerb6.dto.request.LineRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LineResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/column")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Column Api", description = "All endpoints of column")
public class ColumnApi {

    private final ColumnService columnService;

    @Operation(summary = "Create column", description = "Create new column")
    @PostMapping
    public LineResponse createLine(@RequestBody LineRequest lineRequest){
        return columnService.createLine(lineRequest);
    }

    @Operation(summary = "Update column", description = "Update column title by id")
    @PutMapping("update/{id}")
    public LineResponse updateLine(@PathVariable Long id,
                                   @RequestBody LineRequest lineRequest){
        return columnService.updateLine(id, lineRequest);
    }

    @Operation(summary = "Delete column", description = "Delete column by column id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteLine(@PathVariable Long id){
        return columnService.deleteLine(id);
    }

    @Operation(summary = "Send to archive", description = "Send column to archive")
    @PutMapping("archive/{id}")
    public LineResponse addToArchive(@PathVariable Long id){
        return columnService.addToArchive(id);
    }

    @Operation(summary = "Unarchive", description = "Unarchive column by id")
    @PutMapping("unarchive/{id}")
    public LineResponse sendToBoard(@PathVariable Long id){
        return columnService.sendToBoard(id);
    }

    @Operation(summary = "Get all columns", description = "Get all columns by board id")
    @GetMapping("{id}")
    public List<LineResponse> findAllLinesByBoardId(@PathVariable Long id){
        return columnService.findAllLines(id);
    }

    @Operation(summary = "Get archived columns", description = "Get all archived columns")
    @GetMapping("archive-columns")
    public List<LineResponse> findAllLinesByArchive(){
        return columnService.findAllLinesByArchive();
    }
}
