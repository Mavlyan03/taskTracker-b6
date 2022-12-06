package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.ColumnRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ColumnResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ColumnService {

    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BasketRepository basketRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(
                () -> {
                    log.error("User not found!");
                    throw new NotFoundException("User not found!");
                }
        );
    }

    public ColumnResponse createColumn(ColumnRequest columnRequest) {
        User user = getAuthenticateUser();
        Column column = new Column();
        column.setTitle(columnRequest.getColumnName());
        Board board = boardRepository.findById(columnRequest.getBoardId()).orElseThrow(
                () -> {
                    log.error("Board with id: {} not found", column.getBoard().getId());
                    throw new NotFoundException("Board with id: " + column.getBoard().getId() + " not found");
                }
        );

        board.addColumn(column);
        column.setBoard(board);
        column.setCreator(user);
        Column column1 = columnRepository.save(column);
        log.info("Column successfully created");
        return columnRepository.getColumnResponse(column1.getId());
    }

    public ColumnResponse updateColumn(Long id, String newTitle) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Column with id: {} not found", id);
                    throw new NotFoundException("Column with id: " + id + " not found");
                }
        );

        if (!column.getCreator().equals(user)) {
            throw new BadCredentialException("You can not update title this of this column!");
        }

        column.setTitle(newTitle);
        Column column1 = columnRepository.save(column);
        log.info("Column title with id: {} successfully updated", id);
        return columnRepository.getColumnResponse(column1.getId());
    }

    public SimpleResponse deleteColumn(Long id) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Column with id: " + id + " not found!");
                    throw new NotFoundException("Column with id: " + id + " not found!");
                }
        );

        if (!column.getCreator().equals(user)) {
            throw new BadCredentialException("You can not delete this column!");
        }

        columnRepository.delete(column);
        log.error("Column with id: {} successfully deleted", id);
        return new SimpleResponse("Column with id: " + id + " successfully deleted", "DELETE");
    }

    public List<ColumnResponse> findAllColumns(Long id) {
        List<Column> columns = columnRepository.findAllColumns(id);
        List<ColumnResponse> columnResponses = new ArrayList<>();
        for (Column column : columns) {
            columnResponses.add(columnRepository.getColumnResponse(column.getId()));
        }

        log.info("Get all columns");
        return columnResponses;
    }


    public ColumnResponse addToArchive(Long id) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Column with id: {} not found!", id);
                    throw new NotFoundException("Column with id: " + id + " not found!");
                }
        );

        Board board = boardRepository.findById(column.getBoard().getId()).get();
        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).get();

        if (!column.getCreator().equals(user) || !workspace.getLead().equals(user)) {
            throw new BadCredentialException("You can not archive this column!");
        }

        column.setIsArchive(!column.getIsArchive());
        if (column.getIsArchive().equals(true)) {
            Basket basket = new Basket();
            basket.setArchivedUser(user);
            basket.setColumn(column);
            basket.setArchivedUser(user);
            basketRepository.save(basket);
        }

        if (column.getIsArchive().equals(false)) {
            List<Basket> baskets = user.getBaskets();
            if (baskets != null) {
                for (Basket b : baskets) {
                    if (b.getColumn() != null && b.getColumn().equals(column)) {
                        basketRepository.deleteBasket(b.getId());
                    }
                }
            }
        }

        Column column1 = columnRepository.save(column);
        log.info("Column with id: {} successfully archived", id);
        return columnRepository.getColumnResponse(column1.getId());
    }


    public List<ColumnResponse> findAllArchivedColumns() {
        List<Column> columns = columnRepository.findAllArchivedColumns();
        List<ColumnResponse> columnResponses = new ArrayList<>();
        for (Column column : columns) {
            columnResponses.add(columnRepository.getColumnResponse(column.getId()));
        }

        log.info("Get all archived columns");
        return columnResponses;
    }

    public ColumnResponse sendToBoard(Long id) {
        Column column = columnRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Column with id: {} not found!", id);
                    throw new NotFoundException("Column with id: " + id + " not found!");
                }
        );

        column.setIsArchive(false);
        log.info("Column with id: {} successfully unarchive", id);
        return columnRepository.getColumnResponse(column.getId());
    }
}
