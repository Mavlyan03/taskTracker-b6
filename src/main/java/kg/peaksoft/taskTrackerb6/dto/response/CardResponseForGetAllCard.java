package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CardResponseForGetAllCard {

    private Long id;
    private String title;
    private List<LabelResponse> labelResponses;
    private String duration;
    private int numberOfMembers;
    private int numberOfSubTasks;
    private int numberOfCompletedSubTask;

    public CardResponseForGetAllCard(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
    }
}
