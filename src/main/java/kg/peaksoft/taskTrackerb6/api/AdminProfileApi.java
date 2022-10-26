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
@RequestMapping("api/admin/profile")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Admin profile Api", description = "All endpoints of admin profile")
public class AdminProfileApi {

    private final AdminService adminService;

    @Operation(summary = "Get profile", description = "Get profile by id")
    @GetMapping("{id}")
    public AdminProfileResponse getProfile(@PathVariable Long id) {
        return adminService.adminProfile(id);
    }

    @Operation(summary = "Update profile", description = "Update profile by profile id")
    @PutMapping("update")
    public AdminProfileResponse updateAdminProfile(@RequestBody AdminProfileRequest request) {
        return adminService.updateUserEntity(request);
    }

    @Operation(summary = "Change photo", description = "Change profile photo by profile id")
    @PutMapping("change-photo/{id}")
    public AdminProfileResponse changeProfilePhoto(@PathVariable Long id,
                                                   @RequestBody String photo) {
        return adminService.changePhoto(id, photo);
    }

    @Operation(summary = "Delete photo", description = "Delete profile photo by profile id")
    @PutMapping("remove-photo/{id}")
    public AdminProfileResponse removePhoto(@PathVariable Long id) {
        return adminService.removePhoto(id);
    }
}
