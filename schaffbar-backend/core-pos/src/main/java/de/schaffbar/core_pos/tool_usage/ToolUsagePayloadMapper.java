package de.schaffbar.core_pos.tool_usage;

import de.schaffbar.core_pos.shared.event.payload.ToolUsagePayloads.ToolUsageStartedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolUsagePayloads.ToolUsageStoppedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ToolUsagePayloadMapper {

    ToolUsagePayloadMapper MAPPER = Mappers.getMapper(ToolUsagePayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    ToolUsageStartedPayload toToolUsageStartedPayload(ToolUsage toolUsage);

    ToolUsageStoppedPayload toToolUsageStoppedPayload(ToolUsage toolUsage);

}
