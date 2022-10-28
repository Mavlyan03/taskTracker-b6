package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.AdminService;
import kg.peaksoft.taskTrackerb6.dto.request.AdminProfileRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AdminProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/profile")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Profile API", description = "All endpoints of admin profile")
public class ProfileApi {

    private final AdminService adminService;

    @Operation(summary = "Get profile", description = "Get profile by id")
    @GetMapping("{id}")
    public AdminProfileResponse getProfile(@PathVariable Long id) {
        return adminService.adminProfile(id);
    }

    @Operation(summary = "Update profile", description = "Update profile by profile id")
    @PutMapping
    public AdminProfileResponse updateAdminProfile(@RequestBody AdminProfileRequest request) {
        return adminService.updateUserEntity(request);
    }
}
