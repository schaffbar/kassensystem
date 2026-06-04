package de.schaffbar.core_pos.domain.tool;

import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolCreatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolUpdatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolWlanRelaisSetPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ToolPayloadMapper {

    ToolPayloadMapper MAPPER = Mappers.getMapper(ToolPayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    ToolCreatedPayload toToolCreatedPayload(Tool tool);

    ToolUpdatedPayload toToolUpdatedPayload(Tool tool);

    ToolWlanRelaisSetPayload toToolWlanRelaisSetPayload(Tool tool);

}
