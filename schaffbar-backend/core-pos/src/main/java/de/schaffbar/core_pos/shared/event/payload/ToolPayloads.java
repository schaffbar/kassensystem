package de.schaffbar.core_pos.shared.event.payload;

import java.util.List;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface ToolPayloads {

    record ToolCreatedPayload( //
            @Valid @NotNull ToolId id, //
            @NotBlank String name, //
            String description, //
            RfidReaderId rfidReaderId, //
            String wlanRelaisType, //
            String ipAddress //
    ) implements ToolEventPayload {}

    record ToolUpdatedPayload( //
            @Valid @NotNull ToolId id, //
            @NotBlank String name, //
            String description //
    ) implements ToolEventPayload {}

    record ToolWlanRelaisUpdatedPayload( //
            @Valid @NotNull ToolId id, //
            String wlanRelaisType, //
            String ipAddress //
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
