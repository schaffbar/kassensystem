package de.schaffbar.core_pos.token;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.TokenId;
import de.schaffbar.core_pos.token.commands.RequestTokenAssignmentCommand;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TokenAssignmentService {

    private final @NonNull TokenAssignmentRepository tokenAssignmentRepository;

    // ------------------------------------------------------------------------
    // query

    public List<TokenAssignmentView> getTokenAssignments() {
        return tokenAssignmentRepository.findAll().stream() //
                .map(TokenAssignmentViewMapper.MAPPER::toTokenAssignmentView) //
                .toList();
    }

    public Optional<TokenAssignmentView> getTokenAssignment(@NotNull @Valid CustomerId customerId) {
        return tokenAssignmentRepository.findByCustomer(customerId) //
                .map(TokenAssignmentViewMapper.MAPPER::toTokenAssignmentView);
    }

    public Optional<TokenAssignmentView> getTokenAssignment(@NotNull @Valid TokenId tokenId) {
        return tokenAssignmentRepository.findByToken(tokenId) //
                .map(TokenAssignmentViewMapper.MAPPER::toTokenAssignmentView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void requestTokenAssignment(@NotNull @Valid RequestTokenAssignmentCommand command) {
        if (getTokenAssignment(command.getCustomerId()).isPresent()) {
            throw new RuntimeException("Token assignment already exists for customer with id: " + command.getCustomerId());
        }

        if (this.tokenAssignmentRepository.findWaitingForAssignment().isPresent()) {
            throw new RuntimeException("There is already a token assignment pending");
        }

        TokenAssignment tokenAssignment = TokenAssignment.of(command);
        this.tokenAssignmentRepository.save(tokenAssignment);
    }

    @Transactional
    public void assignToken(@NotNull @Valid TokenId tokenId) {
        if (getTokenAssignment(tokenId).isPresent()) {
            throw new RuntimeException("Token assignment with token " + tokenId + " already exists");
        }

        this.tokenAssignmentRepository.findWaitingForAssignment() //
                .ifPresentOrElse( //
                        assignment -> assignment.assignToken(tokenId), //
                        throwNoWaitingAssignmentFound());
    }

    // ------------------------------------------------------------------------
    // helper

    private Runnable throwNoWaitingAssignmentFound() {
        return () -> {
            throw new RuntimeException("No waiting for token assignment found");
        };
    }

}
