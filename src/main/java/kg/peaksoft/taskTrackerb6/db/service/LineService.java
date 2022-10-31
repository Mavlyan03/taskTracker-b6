package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Line;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.LineRepository;
import kg.peaksoft.taskTrackerb6.dto.request.LineRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LineResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LineService {

    private final BoardRepository boardRepository;
    private final LineRepository lineRepository;

    public LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line();
        line.setTitle(lineRequest.getLineName());
        Board board = boardRepository.findById(lineRequest.getBoardId()).orElseThrow(
                () -> new NotFoundException("Board with id: " + line.getBoard().getId() + " not found")
        );
        board.addLine(line);
        line.setBoard(board);
        Line line1 = lineRepository.save(line);
        return new LineResponse(line1.getId(), line1.getTitle(), line1.getBoard().getId());
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );
        line.setTitle(lineRequest.getLineName());
        Board board = boardRepository.findById(line.getBoard().getId()).orElseThrow(
                () -> new NotFoundException("Board with id: " + line.getBoard().getId() + " not found")
        );
        board.addLine(line);
        line.setBoard(board);
        Line line1 = lineRepository.save(line);
        return new LineResponse(line1.getId(), line1.getTitle(), line1.getBoard().getId());
    }

    public SimpleResponse deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );
        lineRepository.delete(line);
        return new SimpleResponse("Line with id: " + id + " successfully deleted", "DELETED");
    }

    public List<LineResponse> findAllLines(Long id) {
        List<Line> lines = lineRepository.findAllLines(id);
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(convertToResponse(line));
        }
        return lineResponses;
    }

    public LineResponse convertToResponse(Line line) {
        LineResponse lineResponse = new LineResponse(line.getId(), line.getTitle(), line.getBoard().getId());
        return lineResponse;
    }

    public LineResponse addToArchive(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );
        line.setIsArchive(true);
        Line line1 = lineRepository.save(line);
        return convertToResponse(line1);
    }

    public List<LineResponse> findAllLinesByArchive() {
        List<Line> lines = lineRepository.findAllByIsArchive();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(convertToResponse(line));
        }
        return lineResponses;
    }

    public LineResponse sendToBoard(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Line with id: " + id + " not found")
        );
        line.setIsArchive(false);
        return convertToResponse(lineRepository.save(line));
    }
}
