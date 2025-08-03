package de.schaffbar.core_pos.token_assignment.web;

import de.schaffbar.core_pos.ValueObjectMapper;
import de.schaffbar.core_pos.token_assignment.TokenAssignmentCommands.RequestTokenAssignmentCommand;
import de.schaffbar.core_pos.token_assignment.TokenAssignmentViews.TokenAssignmentView;
import de.schaffbar.core_pos.token_assignment.web.TokenAssignmentApiModel.RequestTokenAssignmentRequestBody;
import de.schaffbar.core_pos.token_assignment.web.TokenAssignmentApiModel.TokenAssignmentApiDto;
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

}
