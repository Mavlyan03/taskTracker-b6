package kg.peaksoft.taskTrackerb6.api;

import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.MemberService;
import kg.peaksoft.taskTrackerb6.db.service.UserService;
import kg.peaksoft.taskTrackerb6.dto.request.ResetPasswordRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignInRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignUpRequest;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/public")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Auth API", description = "Registration and authentication")
public class AuthApi {

    private final UserService userService;
    private final MemberService memberService;

    @Operation(summary = "Sign up", description = "Any user can register")
    @PostMapping("registration")
    public AuthResponse registration(@RequestBody @Valid SignUpRequest signUpRequest) {
        return userService.registration(signUpRequest);
    }

    @Operation(summary = "Sign in", description = "Only registered users can login")
    @PostMapping("login")
    public AuthResponse login(@RequestBody @Valid SignInRequest signInRequest) {
        return userService.login(signInRequest);
    }

    @Operation(summary = "Forgot password", description = "If the user has forgotten the password")
    @PostMapping("forgot/password")
    public SimpleResponse forgotPassword(@RequestParam String email,
                                         @RequestParam String link) throws MessagingException {
        return userService.forgotPassword(email, link);
    }

    @Operation(summary = "Reset password", description = "Allows you to reset the user's password")
    @PostMapping("reset/password")
    public ResetPasswordResponse resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }

    @Operation(summary = "Google authentication", description = "Any user can authenticate with Google")
    @PostMapping("authenticate/google")
    public AuthResponse authWithGoogleAccount(@RequestParam String token) throws FirebaseAuthException {
        return userService.authWithGoogle(token);
    }

    @Operation(summary = "Search members", description = "Search members by workspace id")
    @GetMapping("/global-search/{id}")
    public List<MemberResponse> globalSearch(@PathVariable Long id,
                                             @RequestParam String email) {
        return memberService.searchByEmailOrName(id, email);
    }
}
