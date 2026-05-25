package de.schaffbar.core_pos.shared.event.payload;

import java.time.Instant;

import de.schaffbar.core_pos.shared.event.EventPayload;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.ToolUsageId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface ToolUsageEventPayload extends EventPayload {

	record ToolUsageStartedPayload( //
			@Valid @NotNull ToolUsageId id, //
			@Valid @NotNull CustomerId customerId, //
			@Valid @NotNull ToolId toolId, //
			@Valid @NotNull WorkshopSessionId workshopSessionId, //
			@NotNull Instant startTime //
	) implements ToolUsageEventPayload {}

	record ToolUsageStoppedPayload( //
			@Valid @NotNull ToolUsageId id, //
			@Valid @NotNull CustomerId customerId, //
			@Valid @NotNull ToolId toolId, //
			@Valid @NotNull WorkshopSessionId workshopSessionId, //
			@NotNull Instant startTime, //
			@NotNull Instant endTime //
	) implements ToolUsageEventPayload {}

}
