package de.schaffbar.core_pos.token.web;

import de.schaffbar.core_pos.ValueObjectMapper;
import de.schaffbar.core_pos.token.TokenAssignmentCommands.RequestTokenAssignmentCommand;
import de.schaffbar.core_pos.token.TokenAssignmentViews.TokenAssignmentView;
import de.schaffbar.core_pos.token.web.TokenAssignmentApiModel.RequestTokenAssignmentRequestBody;
import de.schaffbar.core_pos.token.web.TokenAssignmentApiModel.TokenAssignmentApiDto;
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
