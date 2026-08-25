package br.com.jhonatan.consumer.controller;

import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.SubscriptionDetails;
import br.com.jhonatan.consumer.service.SubscriptionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription lifecycle actions consumed via the provider API")
public class SubscriptionsController {

    private final SubscriptionsService subscriptionsService;
    
    @Operation(
            summary = "List the users subscriptions",
            description = "Returns all subscriptions linked to the users identified by document."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of the document subscriptions",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubscriptionDetails.class)))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public List<SubscriptionDetails> listSubscriptions(
            @Parameter(description = "User document", example = "000.000.000-00")
            @PathVariable String document) {
        return subscriptionsService.list(document);
    }

    @Operation(
            summary = "Activate a subscription",
            description = "Activates the given subscription for the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation result",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "User or subscription not found")
    })
    @PostMapping("/{subscriptionCode}/activate")
    public StatusResponse activate(
            @Parameter(description = "User document", example = "000.000.000-00")
            @PathVariable String document,
            @Parameter(description = "Subscription code", example = "PLANO-PREMIUM")
            @PathVariable String subscriptionCode,
            @RequestBody ContactRequest request) {
        return subscriptionsService.activate(document, subscriptionCode, request);
    }

    @Operation(
            summary = "Cancel a subscription",
            description = "Cancels the given subscription for the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription canceled successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or subscription not found")
    })
    @DeleteMapping("/{subscriptionCode}/cancellation")
    public StatusResponse cancel(
            @Parameter(description = "User document", example = "000.000.000-00")
            @PathVariable String document,
            @Parameter(description = "Subscription code", example = "PLANO-PREMIUM")
            @PathVariable String subscriptionCode) {
        return subscriptionsService.cancel(document, subscriptionCode);
    }

    @Operation(
            summary = "Block a subscription",
            description = "Blocks the given subscription for the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription blocked successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or subscription not found")
    })
    @PostMapping("/{subscriptionCode}/block")
    public StatusResponse block(
            @Parameter(description = "User document", example = "jhonatan.silva")
            @PathVariable String document,
            @Parameter(description = "Subscription code", example = "PLANO-PREMIUM")
            @PathVariable String subscriptionCode) {
        return subscriptionsService.block(document, subscriptionCode);
    }

    @Operation(
            summary = "Unblock a subscription",
            description = "Unblocks the given subscription for the user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription unblocked successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or subscription not found")
    })
    @PostMapping("/{subscriptionCode}/unblock")
    public StatusResponse unblock(
            @Parameter(description = "User document", example = "jhonatan.silva")
            @PathVariable String document,
            @Parameter(description = "Subscription code", example = "PLANO-PREMIUM")
            @PathVariable String subscriptionCode) {
        return subscriptionsService.unblock(document, subscriptionCode);
    }

    @Operation(
            summary = "Update the contact information tied to a subscription",
            description = "Updates the email/phone associated with the user subscription."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact information updated successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "User or subscription not found")
    })
    @PutMapping("/{subscriptionCode}")
    public StatusResponse updateContact(
            @Parameter(description = "User document", example = "000.000.000-00")
            @PathVariable String document,
            @Parameter(description = "Subscription code", example = "PLANO-PREMIUM")
            @PathVariable String subscriptionCode,
            @RequestBody ContactRequest request) {
        return subscriptionsService.updateContact(document, subscriptionCode, request);
    }
}
