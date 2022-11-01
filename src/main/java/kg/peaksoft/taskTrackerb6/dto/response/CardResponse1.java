package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardResponse1 {

    private Long id;
    private String title;

    public CardResponse1(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
    }

}
