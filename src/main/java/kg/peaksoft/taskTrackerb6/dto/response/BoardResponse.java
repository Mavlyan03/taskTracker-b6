package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {

    private Long id;
    private String title;
    private Boolean isFavorite;
    private String background;
}