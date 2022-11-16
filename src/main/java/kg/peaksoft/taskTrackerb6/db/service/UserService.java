package kg.peaksoft.taskTrackerb6.db.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import kg.peaksoft.taskTrackerb6.config.security.JWTUtil;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.ResetPasswordRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignInRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignUpRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AuthResponse;
import kg.peaksoft.taskTrackerb6.dto.response.ResetPasswordResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SearchResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.BadRequestException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public AuthResponse registration(SignUpRequest signUpRequest) {

        if (repository.existsUserByEmail(signUpRequest.getEmail())) {
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
                () -> new NotFoundException("user with this email: " + signInRequest.getEmail() + " not found!"));

        if (signInRequest.getPassword().isBlank()) {
            throw new BadRequestException("password can not be empty!");
        }

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialException("incorrect password");
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
                () -> new NotFoundException("User with email: " + email + " not found!")
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
                () -> new NotFoundException("user with id: " + request.getUserId() + " not found")
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

    public AuthResponse authWithGoogle(String tokenId) throws FirebaseAuthException {
        FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(tokenId);
        User user;
        if (!repository.existsUserByEmail(firebaseToken.getEmail())) {
            User newUser = new User();
            String[] name = firebaseToken.getName().split(" ");
            newUser.setFirstName(name[0]);
            newUser.setLastName(name[1]);
            newUser.setEmail(firebaseToken.getEmail());
            newUser.setPassword(firebaseToken.getEmail());
            newUser.setRole(Role.ADMIN);
            user = repository.save(newUser);
        }
        user = repository.findUserByEmail(firebaseToken.getEmail()).orElseThrow(
                () -> new NotFoundException("user with this email not found!"));
        String token = jwtUtil.generateToken(user.getPassword());
        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                token);
    }

    private SearchResponse mapToSearchResponse(User user) {
        return new SearchResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public List<SearchResponse> globalSearch(String text) {
        List<SearchResponse> searchResponses= new ArrayList<>();
        List<User> users = repository.globalSearch(text);
        for (User user : users) {
            searchResponses.add(mapToSearchResponse(user)
            );
        }
        return searchResponses;
    }
}