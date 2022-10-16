package kg.peaksoft.taskTrackerb6.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import kg.peaksoft.taskTrackerb6.dto.request.ResetPasswordRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignInRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignUpRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AuthResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.entities.User;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadRequestException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import kg.peaksoft.taskTrackerb6.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.configs.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public AuthResponse registration(SignUpRequest signUpRequest) {

        if (signUpRequest.getPassword().isBlank()) {
            throw new BadRequestException("password can not be empty!");
        }
        if (repository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("this email: " + signUpRequest.getEmail() + " is already in use!");
        }

        User user = convertToRegisterEntity(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.USER);
        repository.save(user);

        String jwt = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                jwt
        );
    }

    public AuthResponse login(SignInRequest signInRequest) {

        if (signInRequest.getPassword().isBlank()) {
            throw new BadRequestException("password can not be empty!");
        }

        User user = repository.findByEmail(signInRequest.getEmail()).orElseThrow(() -> new NotFoundException("user with this email: " + signInRequest.getEmail() + " not found!"));

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("incorrect password");
        }

        String jwt = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                jwt
        );
    }

    public SimpleResponse forgotPassword(String email, String link) throws MessagingException {
        User user = repository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email: " + email + " not found!")
        );
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setSubject("[task_tracker] reset password link");
        helper.setFrom("tasktracker.b6@gmail.com");
        helper.setTo(email);
        helper.setText(link + "/" + user.getId(), true);
        mailSender.send(mimeMessage);
        return new SimpleResponse("email send", "ok");
    }

    public SimpleResponse resetPassword(ResetPasswordRequest request) {
        User user = repository.findById(request.getUserId()).orElseThrow(
                () -> new NotFoundException("user with id: " + request.getUserId() + " not found")
        );
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return new SimpleResponse("password updated ", "ok");
    }

    private User convertToRegisterEntity(SignUpRequest signUpRequest) {
        return User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .build();
    }

    public AuthResponse authWithGoogle(String tokenId) throws FirebaseAuthException {
        FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(tokenId);
        User user;
        if (!repository.existsByEmail(firebaseToken.getEmail())) {
            User newUser = new User();
            newUser.setFirstName(firebaseToken.getName());
            newUser.setEmail(firebaseToken.getEmail());
            newUser.setPassword(firebaseToken.getEmail());
            newUser.setRole(Role.USER);
            user = repository.save(newUser);
        }
        user = repository.findByEmail(firebaseToken.getEmail()).orElseThrow(() -> new NotFoundException("user with this email not found!"));
        String token = jwtUtil.generateToken(user.getPassword());
        return new AuthResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(), token);
    }

}
