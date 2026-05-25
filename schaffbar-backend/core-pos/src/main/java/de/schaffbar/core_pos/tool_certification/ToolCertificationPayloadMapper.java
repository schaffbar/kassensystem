package de.schaffbar.core_pos.tool_certification;

import de.schaffbar.core_pos.shared.event.payload.ToolCertificationEventPayload.ToolCertificationDeletedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolCertificationEventPayload.ToolCertificationCreatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolCertificationEventPayload.ToolCertificationPausedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolCertificationEventPayload.ToolCertificationReactivatedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolCertificationEventPayload.ToolCertificationRevokedPayload;
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

    ToolCertificationDeletedPayload toToolCertificationDeletedPayload(ToolCertification toolCertification);

}
