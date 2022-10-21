package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequest {

    private Long workspaceId;

    private String title;

    private String background;
}
