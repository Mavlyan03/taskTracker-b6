package kg.peaksoft.taskTrackerb6.db.service;


import kg.peaksoft.taskTrackerb6.db.model.Favorite;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.response.FavoriteResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;


    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }

    public List<FavoriteResponse> getAllUserFavoriteWorkspacesAndBoards() {
        User user = getAuthenticateUser();
        List<Favorite> favorites = user.getFavorites();
        List<FavoriteResponse> favoriteResponses = new ArrayList<>();
        for (Favorite favorite : favorites) {
            if (favorite.getBoard() != null) {
                favoriteResponses.add(new FavoriteResponse(favorite.getBoard()));
            } else if (favorite.getWorkspace() != null) {
                favoriteResponses.add(new FavoriteResponse(favorite.getWorkspace()));
            }
        }

        return favoriteResponses;
    }
}
