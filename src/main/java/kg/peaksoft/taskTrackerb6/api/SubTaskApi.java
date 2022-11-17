package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/subtasks")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Subtask API", description = "All endpoints of subtask")
public class SubTaskApi {


}
