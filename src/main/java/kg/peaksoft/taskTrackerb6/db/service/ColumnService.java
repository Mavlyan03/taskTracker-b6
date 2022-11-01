package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Column;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.ColumnRepository;
import kg.peaksoft.taskTrackerb6.dto.request.LineRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LineResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;

    public LineResponse createLine(LineRequest lineRequest) {
        Column line = new Column();
        line.setTitle(lineRequest.getLineName());
        Board board = boardRepository.findById(lineRequest.getBoardId()).orElseThrow(
                () -> new NotFoundException("Board with id: " + line.getBoard().getId() + " not found")
        );

        board.addLine(line);
        line.setBoard(board);
        Column line1 = columnRepository.save(line);
        return new LineResponse(line1.getId(), line1.getTitle(), line1.getBoard().getId());
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Column line = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );

        line.setTitle(lineRequest.getLineName());
        Board board = boardRepository.findById(line.getBoard().getId()).orElseThrow(
                () -> new NotFoundException("Board with id: " + line.getBoard().getId() + " not found")
        );

        board.addLine(line);
        line.setBoard(board);
        Column line1 = columnRepository.save(line);
        return new LineResponse(line1.getId(), line1.getTitle(), line1.getBoard().getId());
    }

    public SimpleResponse deleteLine(Long id) {
        Column line = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );

        columnRepository.delete(line);
        return new SimpleResponse("Line with id: " + id + " successfully deleted", "DELETED");
    }

    public List<LineResponse> findAllLines(Long id) {
        List<Column> lines = columnRepository.findAllColumns(id);
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Column line : lines) {
            lineResponses.add(convertToResponse(line));
        }

        return lineResponses;
    }

    public LineResponse convertToResponse(Column line) {
        return new LineResponse(line.getId(), line.getTitle(), line.getBoard().getId());
    }

    public LineResponse addToArchive(Long id) {
        Column line = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );

        line.setIsArchive(true);
        Column line1 = columnRepository.save(line);
        return convertToResponse(line1);
    }

    public List<LineResponse> findAllLinesByArchive() {
        List<Column> lines = columnRepository.findAllArchivedColumns();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Column line : lines) {
            lineResponses.add(convertToResponse(line));
        }

        return lineResponses;
    }

    public LineResponse sendToBoard(Long id) {
        Column line = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );

        line.setIsArchive(false);
        return convertToResponse(columnRepository.save(line));
    }
}
