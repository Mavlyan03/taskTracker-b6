package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardMemberResponse {

    private Long id;
    private String image;

    public CardMemberResponse(User user) {
        this.id = user.getId();
        this.image = user.getImage();
    }
}
