package de.schaffbar.core_pos.tool_certification;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolCertificationId;
import de.schaffbar.core_pos.shared.id.ToolId;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolCertificationViews {

    ToolCertificationViews MAPPER = Mappers.getMapper(ToolCertificationViews.class);

    // ------------------------------------------------------------------------
    // mapper

    ToolCertificationView toToolCertificationView(ToolCertification toolCertification);

    // ------------------------------------------------------------------------
    // views

    record ToolCertificationView( //
            @NotNull ToolCertificationId id, //
            @NotNull CustomerId customerId, //
            @NotNull ToolId toolId, //
            @NotNull ToolCertificationStatus status, //
            @NotNull Instant certifiedAt, //
            CustomerId certifiedBy //
    ) {}

}
