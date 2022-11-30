package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.MemberService;
import kg.peaksoft.taskTrackerb6.dto.response.AllMemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.tainting.qual.PolyTainted;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Member API", description = "Member endpoints for Admin")
public class MemberApi {

    private final MemberService memberService;

    @GetMapping("/search/{id}")
    @Operation(summary = "Search members by email or name",
            description = "Admin search members by email or name")
    public List<MemberResponse> searchByEmailOrName(@PathVariable Long id,
                                                    @RequestParam String email) {
        return memberService.searchByEmailOrName(id, email);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get all members",
            description = "Admin get all members by card id")
    public AllMemberResponse getAllMembers(@PathVariable Long id) {
        return memberService.getAllMembers(id);
    }

    @PostMapping("/assign/{id}/{cardId}")
    @Operation(summary = "Assign member to card",
            description = "Admin assign member to card")
    public MemberResponse assignMemberToCard(@PathVariable Long id,
                                             @PathVariable Long cardId) {
        return memberService.assignMemberToCard(id, cardId);
    }
}
