package de.schaffbar.core_pos.token;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TokenAssignmentViewMapper {

    TokenAssignmentViewMapper MAPPER = Mappers.getMapper(TokenAssignmentViewMapper.class);

    TokenAssignmentView toTokenAssignmentView(TokenAssignment tokenAssignment);

}
