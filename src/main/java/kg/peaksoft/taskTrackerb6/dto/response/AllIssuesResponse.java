package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllIssuesResponse {

    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAt;
    private String period;
    private Long creatorId;
    private String creatorFirstName;
    private String creatorLastName;
    private String column;
    private List<CardMemberResponse> assignee;
    private List<LabelResponse> labels;
    private String checklist;
    private String description;

    public AllIssuesResponse(Card card) {
        this.id = card.getId();
        this.createdAt = card.getCreatedAt();
        this.creatorId = card.getCreator().getId();
        this.creatorFirstName = card.getCreator().getFirstName();
        this.creatorLastName = card.getCreator().getLastName();
        this.column = card.getColumn().getTitle();
        this.description = card.getDescription();
    }
}
