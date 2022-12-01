package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllMemberResponse {

    private List<MemberResponse> boardMembers;
    private List<MemberResponse> workspaceMembers;
}
