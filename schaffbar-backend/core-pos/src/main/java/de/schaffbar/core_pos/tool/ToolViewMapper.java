package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolViewMapper {

    ToolViewMapper MAPPER = Mappers.getMapper(ToolViewMapper.class);

    ToolView toToolView(Tool tool);

}
