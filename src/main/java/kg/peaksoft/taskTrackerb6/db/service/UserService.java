package kg.peaksoft.taskTrackerb6.db.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import kg.peaksoft.taskTrackerb6.config.security.JWTUtil;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserWorkSpaceRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.AuthWithGoogleRequest;
import kg.peaksoft.taskTrackerb6.dto.request.ResetPasswordRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignInRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignUpRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AuthResponse;
import kg.peaksoft.taskTrackerb6.dto.response.ResetPasswordResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.BadRequestException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final WorkspaceRepository workspaceRepository;
    private final UserWorkSpaceRepository userWorkSpaceRepository;

    public AuthResponse registration(SignUpRequest signUpRequest) {

        if (repository.existsUserByEmail(signUpRequest.getEmail())) {
            log.error("This email: {} is already in use!", signUpRequest.getEmail());
            throw new BadRequestException("This email: " + signUpRequest.getEmail() + " is already in use!");
        }

        User user = convertToRegisterEntity(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.ADMIN);
        repository.save(user);

        String jwt = jwtUtil.generateToken(user.getEmail());
        log.info("User successfully registered");
        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                jwt
        );
    }

    @PostConstruct
    public void init() throws IOException {
        GoogleCredentials googleCredentials =
                GoogleCredentials.fromStream(new ClassPathResource("tasktracker.json")
                        .getInputStream());

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials).build();

        FirebaseApp.initializeApp(firebaseOptions);
    }

    public AuthResponse login(SignInRequest signInRequest) {

        User user = repository.findUserByEmail(signInRequest.getEmail()).orElseThrow(
                () -> {
                    log.error("User with this email: {} not found!", signInRequest.getEmail());
                    throw new NotFoundException("User with this email: " + signInRequest.getEmail() + " not found!");
                }
        );

        if (signInRequest.getPassword().isBlank()) {
            log.error("Password can not be empty!");
            throw new BadRequestException("password can not be empty!");
        }

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            log.error("incorrect password!");
            throw new BadCredentialException("incorrect password!");
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
        User user = repository.findUserByEmail(email).orElseThrow(
                () -> {
                    log.error("User with email: {} not found!", email);
                    throw new NotFoundException("User with email: " + email + " not found!");
                }
        );

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setSubject("[task_tracker] reset password link , user id: " + user);
        helper.setFrom("tasktracker.b6@gmail.com");
        helper.setTo(email);
        helper.setText(link + "/" + user.getId(), true);
        mailSender.send(mimeMessage);
        return new SimpleResponse("email send", "OK");
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = repository.findById(request.getUserId()).orElseThrow(
                () -> {
                    log.error("User with id: {} not found!", request.getUserId());
                    throw new NotFoundException("User with id: " + request.getUserId() + " not found");
                }
        );

        String oldPassword = user.getPassword();
        String newPassword = passwordEncoder.encode(request.getNewPassword());
        if (!oldPassword.equals(newPassword)) {
            user.setPassword(newPassword);
        }
        String jwt = jwtUtil.generateToken(user.getEmail());

        return new ResetPasswordResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                jwt,
                "Password updated!");
    }

    private User convertToRegisterEntity(SignUpRequest signUpRequest) {
        return User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .build();
    }

    public AuthResponse authWithGoogle(AuthWithGoogleRequest request) throws FirebaseAuthException {
        FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(request.getToken());
        User user;
        if (!repository.existsUserByEmail(firebaseToken.getEmail())) {
            String[] name = firebaseToken.getName().split(" ");
            user = new User();
            user.setFirstName(name[0]);
            user.setLastName(name[1]);
            user.setEmail(firebaseToken.getEmail());
            user.setPassword(firebaseToken.getEmail());
            if (request.getIsAdmin() != null) {
                if (request.getIsAdmin().equals(true)) {
                    user.setRole(Role.ADMIN);
                } else if (request.getIsAdmin().equals(false)) {
                    user.setRole(Role.USER);
                }
            }

            user = repository.save(user);
        }

        user = repository.findUserByEmail(firebaseToken.getEmail()).orElseThrow(
                () -> {
                    log.error("User with this email not found!");
                    throw new NotFoundException("User with this email not found!");
                }
        );

        if (request.getWorkspaceId() != null) {
            Workspace workspace = workspaceRepository.findById(request.getWorkspaceId()).orElseThrow(
                    () -> new NotFoundException("Workspace with id: " + request.getWorkspaceId() + " not found!")
            );

            UserWorkSpace userWorkSpace = new UserWorkSpace(user, workspace, Role.USER);
            userWorkSpaceRepository.save(userWorkSpace);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                token);
    }
}