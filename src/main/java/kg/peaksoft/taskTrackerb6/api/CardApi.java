package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.CardService;
import kg.peaksoft.taskTrackerb6.dto.request.CardRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateCardTitleRequest;
import kg.peaksoft.taskTrackerb6.dto.response.CardResponseForGetAllCard;
import kg.peaksoft.taskTrackerb6.dto.response.CardResponseForGetById;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/card")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Card Api", description = "All endpoints of card")
public class CardApi {

    private final CardService cardService;

    @Operation(summary = "Create card", description = "Create new card")
    @PostMapping
    public CardResponseForGetById createCard(@RequestBody CardRequest request) {
             return cardService.createCard(request);
    }

    @Operation(summary = "Get card", description = "Get card by id")
    @GetMapping("{id}")
    public CardResponseForGetById getCardById(@PathVariable Long id) {
        return cardService.getCard(id);
    }

    @Operation(summary = "Update card title", description = "Update card title by id")
    @PutMapping
    public CardResponseForGetById updateCardTitle(@RequestBody UpdateCardTitleRequest request) {
        return cardService.updateTitle(request);
    }

    @Operation(summary = "Delete card", description = "Delete card by id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteCard(@PathVariable Long id) {
        return cardService.deleteCard(id);
    }

    @Operation(summary = "Sent to archive", description = "Sent to archive by id")
    @PutMapping("archive/{id}")
    public CardResponseForGetById sentToArchive(@PathVariable Long id) {
        return cardService.sentToArchive(id);
    }

    @Operation(summary = "Get all cards", description = "Get all cards by column id")
    @GetMapping("cards/{columnId}")
    public List<CardResponseForGetAllCard> getAllCardsByColumnId(@PathVariable Long columnId) {
        return cardService.getAllCardsByColumnId(columnId);
    }

    @Operation(summary = "Get all archived cards", description = "Get all archived cards by board id")
    @GetMapping("archive-cards/{boardId}")
    public List<CardResponseForGetAllCard> getAllArchivedCardsByBoardId(@PathVariable Long boardId) {
        return cardService.getAllArchivedCardsByBoardId(boardId);
    }
}
