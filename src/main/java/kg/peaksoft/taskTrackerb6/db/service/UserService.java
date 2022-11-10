package kg.peaksoft.taskTrackerb6.db.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import kg.peaksoft.taskTrackerb6.dto.request.ResetPasswordRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignInRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SignUpRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AuthResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.BadRequestException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.config.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender  mailSender;

    public AuthResponse registration(SignUpRequest signUpRequest) {

        if (userRepository.existsUserByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("this email: " + signUpRequest.getEmail() + " is already in use!");
        }

        User user = convertToRegisterEntity(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

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

        User user = userRepository.findUserByEmail(signInRequest.getEmail()).orElseThrow(
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
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email: " + email + " not found!")
        );

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setSubject("[task_tracker] reset password link");
        helper.setFrom("tasktracker.b6@gmail.com");
        helper.setTo(email);
        helper.setText(link + "/" + user.getId(), true);
        mailSender.send(mimeMessage);
        return new SimpleResponse("email send", "OK");
    }

    public SimpleResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new NotFoundException("user with id: " + request.getUserId() + " not found")
        );

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return new SimpleResponse("password updated ", "OK");
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
        if (!userRepository.existsUserByEmail(firebaseToken.getEmail())) {
            User newUser = new User();
            newUser.setFirstName(firebaseToken.getName());
            newUser.setEmail(firebaseToken.getEmail());
            newUser.setPassword(firebaseToken.getEmail());
            newUser.setRole(Role.ADMIN);
            user = userRepository.save(newUser);
        }
        user = userRepository.findUserByEmail(firebaseToken.getEmail()).orElseThrow(
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

    private List<User> searchUsersByName(String name) {
        String text = name == null ? "" : name;
        return userRepository.searchUserByFirstNameAndLastName(text.toUpperCase());
    }
}