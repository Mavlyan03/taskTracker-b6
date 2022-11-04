package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.CardService;
import kg.peaksoft.taskTrackerb6.dto.request.CardRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateCardRequest;
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

    @Operation(summary = "Save card", description = "Save new card")
    @PostMapping
    public CardResponse saveCard(@RequestBody CardRequest cardRequest) {
        return cardService.createCard(cardRequest);
    }

    @Operation(summary = "Get card", description = "Get card by card id")
    @GetMapping("{id}")
    public CardResponse getCardById(@PathVariable Long id) {
        return cardService.getCardById(id);
    }

    @Operation(summary = "Update title", description = "Update card title by card id")
    @PutMapping
    public CardResponse updateCardTitle(@RequestBody UpdateCardRequest request) {
        return cardService.updateCardTitle(request);
    }

    @Operation(summary = "Delete card", description = "Delete card by card id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteCard(@PathVariable Long id) {
        return cardService.deleteCard(id);
    }

    @Operation(summary = "Get all cards with query", description = "Get all cards by column id")
    @GetMapping("cards-with-query/{id}")
    public List<CardResponse> getAllCardsByColumnId(@PathVariable Long id) {
        return cardService.getAllCardsByColumnIdWithQuery(id);
    }

    @Operation(summary = "Send to archive", description = "Sent card to archive by card id")
    @PutMapping("send-to-archive/{id}")
    public CardResponse sentToArchive(@PathVariable Long id) {
        return cardService.sendToArchive(id);
    }

    @Operation(summary = "Get archived cards", description = "Get all archived cards by board id")
    @GetMapping("archived-cards/{id}")
    public List<CardResponse> getAllArchivedCardsByBoardId(@PathVariable Long id) {
        return cardService.getAllArchivedCards(id);
    }
}
