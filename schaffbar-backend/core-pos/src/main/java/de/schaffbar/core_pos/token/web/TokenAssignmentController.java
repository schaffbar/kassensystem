package de.schaffbar.core_pos.token.web;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.TokenId;
import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.view.CustomerView;
import de.schaffbar.core_pos.token.TokenAssignmentService;
import de.schaffbar.core_pos.token.commands.RequestTokenAssignmentCommand;
import de.schaffbar.core_pos.token.web.TokenAssignmentApiMapper.AssignTokenRequestBody;
import de.schaffbar.core_pos.token.web.TokenAssignmentApiMapper.RequestTokenAssignmentRequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/token-assignments")
public class TokenAssignmentController {

    private final @NonNull TokenAssignmentService tokenAssignmentService;

    private final @NonNull CustomerService customerService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TokenAssignmentApiDto>> getAllTokenAssignments( //
            @RequestParam(value = "token-id", required = false) String tokenId, //
            @RequestParam(value = "customer-id", required = false) String customerId) {

        List<TokenAssignmentApiDto> result;
        if (isNotBlank(tokenId)) {
            result = this.tokenAssignmentService.getTokenAssignment(TokenId.of(tokenId)).stream() //
                    .map(TokenAssignmentApiMapper.MAPPER::toTokenAssignmentApiDto) //
                    .toList();
        }
        else if (isNotBlank(customerId)) {
            CustomerId customerIdObj = CustomerId.of(UUID.fromString(customerId));
            CustomerView customer = this.customerService.getCustomer(customerIdObj) //
                    .orElseThrow(() -> ResourceNotFoundException.customer(customerIdObj));

            result = this.tokenAssignmentService.getTokenAssignment(customer.getId()).stream() //
                    .map(TokenAssignmentApiMapper.MAPPER::toTokenAssignmentApiDto) //
                    .toList();
        }
        else {
            result = this.tokenAssignmentService.getTokenAssignments().stream() //
                    .map(TokenAssignmentApiMapper.MAPPER::toTokenAssignmentApiDto) //
                    .toList();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> requestTokenAssignment(@RequestBody @Valid @NotNull RequestTokenAssignmentRequestBody requestBody) {
        Optional<CustomerView> customer = this.customerService.getCustomer(CustomerId.of(requestBody.customerId()));
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RequestTokenAssignmentCommand requestTokenAssignmentCommand = TokenAssignmentApiMapper.MAPPER.toRequestTokenAssignmentCommand(requestBody);
        this.tokenAssignmentService.requestTokenAssignment(requestTokenAssignmentCommand);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignToken(@RequestBody @Valid @NotNull AssignTokenRequestBody requestBody) {
        TokenId tokenId = TokenId.of(requestBody.tokenId());
        this.tokenAssignmentService.assignToken(tokenId);

        return ResponseEntity.ok().build();
    }

}
