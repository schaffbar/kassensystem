package de.schaffbar.core_pos.token.web;

import java.util.UUID;

import de.schaffbar.core_pos.ValueObjectMapper;
import de.schaffbar.core_pos.token.TokenAssignmentView;
import de.schaffbar.core_pos.token.TokenType;
import de.schaffbar.core_pos.token.commands.RequestTokenAssignmentCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TokenAssignmentApiMapper extends ValueObjectMapper {

    TokenAssignmentApiMapper MAPPER = Mappers.getMapper(TokenAssignmentApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "customerId", source = "customerId.value")
    @Mapping(target = "tokenId", source = "tokenId.value")
    TokenAssignmentApiDto toTokenAssignmentApiDto(TokenAssignmentView tokenAssignment);

    // ------------------------------------------------------------------------
    // mapping request body to command

    RequestTokenAssignmentCommand toRequestTokenAssignmentCommand(RequestTokenAssignmentRequestBody requestBody);

    // ------------------------------------------------------------------------
    // request body

    record RequestTokenAssignmentRequestBody(@NotNull UUID customerId, @NotNull TokenType tokenType) {}

    record AssignTokenRequestBody(@NotBlank String tokenId) {}

}
