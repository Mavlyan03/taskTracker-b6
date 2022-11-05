package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "times")
@Getter
@Setter
@NoArgsConstructor
public class MyTimeClass {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "time_gen")
    @SequenceGenerator(name = "time_gen", sequenceName = "time_seq", allocationSize = 1, initialValue = 3)
    private Long id;

    private int hour;

    private int minute;


    public void setTime(int h, int m) {
        hour = ((h >= 0 && h < 24) ? h : 0);
        minute = ((m >= 0 && m < 60) ? m : 0);
    }
}
