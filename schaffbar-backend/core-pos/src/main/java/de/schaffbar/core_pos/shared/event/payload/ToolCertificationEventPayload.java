package de.schaffbar.core_pos.shared.event.payload;

import java.time.Instant;

import de.schaffbar.core_pos.shared.event.EventPayload;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolCertificationId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool_certification.ToolCertificationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface ToolCertificationEventPayload extends EventPayload {

	record ToolCertificationCreatedPayload( //
			@Valid @NotNull ToolCertificationId id, //
			@Valid @NotNull CustomerId customerId, //
			@Valid @NotNull ToolId toolId, //
			@NotNull ToolCertificationStatus status, //
			@NotNull Instant certifiedAt, //
			@Valid CustomerId certifiedBy //
	) implements ToolCertificationEventPayload {}

	record ToolCertificationPausedPayload( //
			@Valid @NotNull ToolCertificationId id, //
			@Valid @NotNull CustomerId customerId, //
			@Valid @NotNull ToolId toolId, //
			@NotNull ToolCertificationStatus status //
	) implements ToolCertificationEventPayload {}

	record ToolCertificationReactivatedPayload( //
			@Valid @NotNull ToolCertificationId id, //
			@Valid @NotNull CustomerId customerId, //
			@Valid @NotNull ToolId toolId, //
			@NotNull ToolCertificationStatus status //
	) implements ToolCertificationEventPayload {}

	record ToolCertificationRevokedPayload( //
			@Valid @NotNull ToolCertificationId id, //
			@Valid @NotNull CustomerId customerId, //
			@Valid @NotNull ToolId toolId, //
			@NotNull ToolCertificationStatus status //
	) implements ToolCertificationEventPayload {}

	record ToolCertificationDeletedPayload( //
			@Valid @NotNull ToolCertificationId id, //
			@Valid @NotNull CustomerId customerId, //
			@Valid @NotNull ToolId toolId //
	) implements ToolCertificationEventPayload {}

}
