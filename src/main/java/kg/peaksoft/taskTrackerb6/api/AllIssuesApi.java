package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.AllIssuesService;
import kg.peaksoft.taskTrackerb6.dto.request.LabelColorRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AllIssuesCardsResponse;
import kg.peaksoft.taskTrackerb6.dto.response.AllIssuesResponse;
import kg.peaksoft.taskTrackerb6.dto.request.CardDatesRequest;
import kg.peaksoft.taskTrackerb6.enums.LabelsColor;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/allIssues")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "All issues API", description = "All endpoints of all issues")
public class AllIssuesApi {

    private final AllIssuesService service;

    @Operation(summary = "Get all workspace cards", description = "Get all workspace cards by workspace id")
    @GetMapping("all-issues/{workspaceId}")
    public List<AllIssuesResponse> allIssues(@PathVariable Long workspaceId) {
        return service.allIssues(workspaceId);
    }

    @Operation(summary = "Filter by created date", description = "Filter cards by created date")
    @GetMapping("dates")
    public AllIssuesCardsResponse filterByCreatedDate(@RequestParam LocalDate fromDate,
                                                      @RequestParam LocalDate to) {
        return service.filterByCreatedDate(fromDate, to);
    }

    @Operation(summary = "Filter by label's color", description = "Filter cards by label's color")
    @GetMapping("colors")
    public List<AllIssuesResponse> filterByLabelColor(@RequestParam(value = "text") LabelColorRequest request) {
        System.out.println("It's api for filter by label");
        return service.filterByLabelColor(request);
    }

    @Operation(summary = "Get member's cards", description = "Get all member's cards")
    @GetMapping("member-cards/{workspaceId}/{id}")
    public List<AllIssuesResponse> getAllMemberAssignedCards(@PathVariable Long workspaceId,
                                                             @PathVariable Long id) {
        return service.getAllMemberAssignedCards(workspaceId, id);
    }
}
