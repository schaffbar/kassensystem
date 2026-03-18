package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.shared.event.payload.ToolPayloads.ToolCreatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolPayloads.ToolUpdatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolPayloads.ToolWlanRelaisUpdatedPayload;
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

    ToolWlanRelaisUpdatedPayload toToolWlanRelaisUpdatedPayload(Tool tool);

}
