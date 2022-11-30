package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@AllArgsConstructor
public class AllMemberResponse {

    private List<MemberResponse> boardMembers;
    private List<MemberResponse> workspaceMembers;
}
