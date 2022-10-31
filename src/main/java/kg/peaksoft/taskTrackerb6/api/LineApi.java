package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.LineService;
import kg.peaksoft.taskTrackerb6.dto.request.LineRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LineResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/line")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Line api", description = "All endpoints of line")
public class LineApi {

    private final LineService lineService;

    @Operation(summary = "create line", description = "create new line")
    @PostMapping
    public LineResponse createLine(@RequestBody LineRequest lineRequest){
        return lineService.createLine(lineRequest);
    }

    @Operation(summary = "update line", description = "update title line")
    @PutMapping("update/{id}")
    public LineResponse updateLine(@PathVariable Long id,
                                   @RequestBody LineRequest lineRequest){
        return lineService.updateLine(id, lineRequest);
    }

    @Operation(summary = "delete line", description = "delete line by line id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteLine(@PathVariable Long id){
        return lineService.deleteLine(id);
    }

    @Operation(summary = "send", description = "send to archive")
    @PutMapping("archive/{id}")
    public LineResponse addToArchive(@PathVariable Long id){
        return lineService.addToArchive(id);
    }

    @Operation(summary = "send", description = "send to board")
    @PutMapping("send-to-board/{id}")
    public LineResponse sendToBoard(@PathVariable Long id){
        return lineService.sendToBoard(id);
    }

    @Operation(summary = "get all", description = "get all lines by board id")
    @GetMapping("det-all/{id}")
    public List<LineResponse> findAllLinesByBoardId(@PathVariable Long id){
        return lineService.findAllLines(id);
    }

    @Operation(summary = "get all", description = "get all lines in archive")
    @GetMapping("get-all-in-archive")
    public List<LineResponse> findAllLinesByArchive(){
        return lineService.findAllLinesByArchive();
    }
}
