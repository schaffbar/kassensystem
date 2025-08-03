package de.schaffbar.core_pos.token_assignment;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.TokenAssignmentId;
import de.schaffbar.core_pos.TokenId;
import de.schaffbar.core_pos.token_assignment.TokenAssignmentCommands.RequestTokenAssignmentCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "TOKEN_ASSIGNMENT", schema = "SCHAFFBAR")
class TokenAssignment {

    @Id
    private UUID id;

    @NotNull
    @Column(unique = true)
    private UUID customerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Column(unique = true)
    private String tokenId;

    private Instant assignmentDate;

    private Instant unassignmentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TokenAssignmentStatus status;

    @Version
    private Instant updateTime;

    // ------------------------------------------------------------------------
    // static constructor

    public static TokenAssignment of(RequestTokenAssignmentCommand command) {
        TokenAssignment result = new TokenAssignment();
        result.setId(UUID.randomUUID());
        result.setCustomerId(command.customerId().getValue());
        result.setTokenType(command.tokenType());
        result.setStatus(TokenAssignmentStatus.WAITING_FOR_ASSIGNMENT);

        return result;
    }

    // ------------------------------------------------------------------------
    // query

    public TokenAssignmentId getId() {
        return TokenAssignmentId.of(this.id);
    }

    public CustomerId getCustomerId() {
        return CustomerId.of(this.customerId);
    }

    public TokenId getTokenId() {
        if (this.tokenId == null) {
            return null;
        }

        return TokenId.of(this.tokenId);
    }

    // ------------------------------------------------------------------------
    // command

    public void assignToken(TokenId tokenId) {
        // TODO: check if token is in the correct state
        this.tokenId = tokenId.getValue();
        this.assignmentDate = Instant.now();
        this.status = TokenAssignmentStatus.ASSIGNED;
    }

    public void requestUnassignment() {
        // TODO: check if token is in the correct state
        this.status = TokenAssignmentStatus.WAITING_FOR_UNASSIGNMENT;
    }

    public void unassignToken() {
        // TODO: check if token is in the correct state
        this.unassignmentDate = Instant.now();
        this.status = TokenAssignmentStatus.UNASSIGNED;
    }

}
