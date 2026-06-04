package de.schaffbar.core_pos.shared.event.payload;

import java.util.List;

import de.schaffbar.core_pos.shared.event.EventPayload;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.IpAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.domain.tool.CertificationRequirement;
import de.schaffbar.core_pos.domain.tool.ToolArea;
import de.schaffbar.core_pos.domain.tool.WlanRelaisType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface ToolEventPayload extends EventPayload {

    record ToolCreatedPayload( //
            @Valid @NotNull ToolId id, //
            @NotBlank String name, //
            String description, //
            @NotNull ToolArea area, //
            @NotNull CertificationRequirement certificationRequirement //
    ) implements ToolEventPayload {}

    record ToolUpdatedPayload( //
            @Valid @NotNull ToolId id, //
            @NotBlank String name, //
            String description, //
            @NotNull ToolArea area, //
            @NotNull CertificationRequirement certificationRequirement //
    ) implements ToolEventPayload {}

    record ToolWlanRelaisSetPayload( //
            @Valid @NotNull ToolId id, //
            @NotNull WlanRelaisType wlanRelaisType, //
            @Valid @NotNull IpAddress ipAddress //
    ) implements ToolEventPayload {}

    record ToolWlanRelaisClearedPayload( //
            @Valid @NotNull ToolId id //
    ) implements ToolEventPayload {}

    record ToolRfidReaderAssignedPayload( //
            @Valid @NotNull ToolId id, //
            @Valid @NotNull RfidReaderId rfidReaderId //
    ) implements ToolEventPayload {}

    record ToolRfidReaderClearedPayload( //
            @Valid @NotNull ToolId id //
    ) implements ToolEventPayload {}

    record ToolInstructorsAddedPayload( //
            @Valid @NotNull ToolId id, //
            @NotEmpty List<@Valid @NotNull CustomerId> instructorIds //
    ) implements ToolEventPayload {}

    record ToolInstructorsRemovedPayload( //
            @Valid @NotNull ToolId id, //
            @NotEmpty List<@Valid @NotNull CustomerId> instructorIds //
    ) implements ToolEventPayload {}

    record ToolDeletedPayload( //
            @Valid @NotNull ToolId id //
    ) implements ToolEventPayload {}

}
