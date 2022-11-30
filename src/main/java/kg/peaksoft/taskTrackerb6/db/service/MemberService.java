package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.response.AllMemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.MemberResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;

    public List<MemberResponse> searchByEmailOrName(Long id, String emailOrName) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Work space not found"));
        return userRepository.searchByEmailOrName(emailOrName,workspace.getId());
    }

    public AllMemberResponse getAllMembers(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card not found"));
        Board board = boardRepository.findById(card.getBoard().getId()).orElseThrow(
                () -> new NotFoundException("Board not found"));
        Workspace workspace = workspaceRepository.findById(card.getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace not found"));
        AllMemberResponse members = new AllMemberResponse();
        List<MemberResponse> boardMembers = new ArrayList<>();
        List<MemberResponse> workspaceMembers = new ArrayList<>();
        for(User user : board.getMembers()) {
            boardMembers.add(new MemberResponse(user));
        }
        for(UserWorkSpace workSpace : workspace.getUserWorkSpaces()) {
            User user = userRepository.findUserByWorkSpaceId(workSpace.getId()).orElseThrow(
                    () -> new NotFoundException("User not found"));
            workspaceMembers.add(new MemberResponse(user));
        }
        members.setBoardMembers(boardMembers);
        members.setWorkspaceMembers(workspaceMembers);
        return members;
    }


    public MemberResponse assignMemberToCard(Long memberId, Long cardId) {
        User user = userRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException("User not found"));
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card not found"));
        card.addMember(user);
        cardRepository.save(card);
        return new MemberResponse(user);
    }

}
