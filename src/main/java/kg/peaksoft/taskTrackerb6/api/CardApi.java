package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.CardService;
import kg.peaksoft.taskTrackerb6.dto.request.CardRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateCardRequest;
import kg.peaksoft.taskTrackerb6.dto.response.CardInnerPageResponse;
import kg.peaksoft.taskTrackerb6.dto.response.CardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Card API", description = "All endpoints of card")
public class CardApi {

    private final CardService cardService;

    @Operation(summary = "Create card", description = "Create new card")
    @PostMapping
    public CardInnerPageResponse createCard(@RequestBody CardRequest request) {
        return cardService.createCard(request);
    }

    @Operation(summary = "Get card", description = "Get card by id")
    @GetMapping("/{id}")
    public CardInnerPageResponse getCardById(@PathVariable Long id) {
        return cardService.getCard(id);
    }

    @Operation(summary = "Update card title", description = "Update card title")
    @PutMapping
    public CardInnerPageResponse updateCardTitle(@RequestBody UpdateCardRequest request) {
        return cardService.updateTitle(request);
    }

    @Operation(summary = "Delete card", description = "Delete card by id")
    @DeleteMapping("/{id}")
    public SimpleResponse deleteCard(@PathVariable Long id) {
        return cardService.deleteCard(id);
    }

    @Operation(summary = "Sent to archive", description = "Sent to archive by id")
    @PutMapping("/archive/{id}")
    public CardInnerPageResponse sentToArchive(@PathVariable Long id) {
        return cardService.sentToArchive(id);
    }

    @Operation(summary = "Get all cards", description = "Get all cards by column id")
    @GetMapping("/column/{columnId}")
    public List<CardResponse> getAllCardsByColumnId(@PathVariable Long columnId) {
        return cardService.getAllCardsByColumnId(columnId);
    }

    @Operation(summary = "Move card", description = "Move card by id")
    @GetMapping("/move-card/{cardId}/{columnId}")
    public List<CardResponse> moveCard(@PathVariable Long cardId,
                                       @PathVariable Long columnId) {
        return cardService.moveCard(cardId, columnId);
    }
}
