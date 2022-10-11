package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_gen")
    @SequenceGenerator(name = "attachment_gen", sequenceName = "attachment_seq", allocationSize = 1)
    private Long id;
    private String documentLink;
    private LocalDateTime attachedDate;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST})
    private Card card;
}
