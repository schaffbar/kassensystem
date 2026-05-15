package de.schaffbar.core_pos.tool_certification;

import de.schaffbar.core_pos.shared.event.payload.ToolCertificationPayloads.ToolCertificationCreatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolCertificationPayloads.ToolCertificationPausedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolCertificationPayloads.ToolCertificationReactivatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolCertificationPayloads.ToolCertificationRevokedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ToolCertificationPayloadMapper {

    ToolCertificationPayloadMapper MAPPER = Mappers.getMapper(ToolCertificationPayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    ToolCertificationCreatedPayload toToolCertificationCreatedPayload(ToolCertification toolCertification);

    ToolCertificationPausedPayload toToolCertificationPausedPayload(ToolCertification toolCertification);

    ToolCertificationReactivatedPayload toToolCertificationReactivatedPayload(ToolCertification toolCertification);

    ToolCertificationRevokedPayload toToolCertificationRevokedPayload(ToolCertification toolCertification);

}
