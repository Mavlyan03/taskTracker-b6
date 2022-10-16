package kg.peaksoft.taskTrackerb6.api;

import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.dto.request.ResetPasswordRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignInRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignUpRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AuthResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.db.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/public")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Auth Api", description = "Authorization and Authentication")
public class AuthApi {

    private final UserService userService;

    @Operation(summary = "Sign up", description = "Any user can register")
    @PostMapping("registration")
    public AuthResponse registration(@RequestBody SignUpRequest signUpRequest) {
        return userService.registration(signUpRequest);
    }

    @Operation(summary = "Sign in", description = "Only registered users can login")
    @PostMapping("login")
    public AuthResponse login(@RequestBody SignInRequest signInRequest) {
        return userService.login(signInRequest);
    }

    @Operation(summary = "Forgot password", description = "If the user has forgotten the password")
    @GetMapping("forgot/password")
    public SimpleResponse forgotPassword(@RequestParam String email,
                                         @RequestParam String link) throws MessagingException {
        return userService.forgotPassword(email, link);
    }

    @Operation(summary = "Reset password", description = "Allows you to reset the user's password")
    @PatchMapping("reset/password")
    public SimpleResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }

    @Operation(summary = "Google authentication", description = "Any user can authenticate with Google")
    @PostMapping("authenticate/google")
    public AuthResponse authWithGoogleAccount(@RequestBody String tokenId) throws FirebaseAuthException {
        return userService.authWithGoogle(tokenId);
    }
}
