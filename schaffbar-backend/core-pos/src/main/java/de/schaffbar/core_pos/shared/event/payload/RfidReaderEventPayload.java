package de.schaffbar.core_pos.shared.event.payload;

import de.schaffbar.core_pos.shared.event.EventPayload;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface RfidReaderEventPayload extends EventPayload {

	record RfidReaderCreatedPayload( //
			@Valid @NotNull RfidReaderId id, //
			@NotBlank String macAddress //
	) implements RfidReaderEventPayload {}

	record RfidReaderUpdatedPayload( //
			@Valid @NotNull RfidReaderId id, //
			@NotNull String type, //
			String name, //
			String socketName //
	) implements RfidReaderEventPayload {}

	record RfidReaderTypeChangedPayload( //
			@Valid @NotNull RfidReaderId id, //
			@NotNull String type //
	) implements RfidReaderEventPayload {}

	record RfidReaderDeletedPayload( //
			@Valid @NotNull RfidReaderId id //
	) implements RfidReaderEventPayload {}

}
