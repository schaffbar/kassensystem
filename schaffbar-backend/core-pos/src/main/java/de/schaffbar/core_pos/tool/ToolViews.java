package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolViews {

    ToolViews MAPPER = Mappers.getMapper(ToolViews.class);

    // ------------------------------------------------------------------------
    // mapper

    ToolView toToolView(Tool tool);

    // ------------------------------------------------------------------------
    // views

    record ToolView( //
            @NotNull ToolId id, //
            @NotBlank String name, //
            String description, //
            RfidReaderId rfidReaderId //
    ) {}

}
