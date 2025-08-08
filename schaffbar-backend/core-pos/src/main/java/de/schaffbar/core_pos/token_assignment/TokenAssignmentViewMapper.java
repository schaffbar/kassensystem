package de.schaffbar.core_pos.token_assignment;

import de.schaffbar.core_pos.token_assignment.TokenAssignmentViews.TokenAssignmentView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface TokenAssignmentViewMapper {

    TokenAssignmentViewMapper MAPPER = Mappers.getMapper(TokenAssignmentViewMapper.class);

    TokenAssignmentView toTokenAssignmentView(TokenAssignment tokenAssignment);

}
