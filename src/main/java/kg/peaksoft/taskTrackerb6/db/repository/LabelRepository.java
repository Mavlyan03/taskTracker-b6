package kg.peaksoft.taskTrackerb6.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import kg.peaksoft.taskTrackerb6.db.model.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {
}