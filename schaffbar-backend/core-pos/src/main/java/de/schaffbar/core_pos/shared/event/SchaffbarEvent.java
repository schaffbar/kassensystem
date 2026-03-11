package de.schaffbar.core_pos.shared.event;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.schaffbar.core_pos.shared.event.customer.CustomerEventPayload;
import de.schaffbar.core_pos.shared.event.rfid_reader.RfidReaderEventPayload;
import de.schaffbar.core_pos.shared.event.rfid_tag.RfidTagEventPayload;
import de.schaffbar.core_pos.shared.event.rfid_tag_assignment.RfidTagAssignmentEventPayload;
import de.schaffbar.core_pos.shared.event.tool.ToolEventPayload;
import de.schaffbar.core_pos.shared.event.workshop_session.WorkshopSessionEventPayload;
import de.schaffbar.core_pos.shared.event.workshop_usage.WorkshopUsageEventPayload;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.RfidTagAssignmentId;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import de.schaffbar.core_pos.shared.id.WorkshopUsageId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class SchaffbarEvent implements ValidationSupport {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @NotBlank
    private UUID id;

    @NotNull
    private EventType type;

    @NotNull
    private EventVersion version;

    @NotNull
    private AggregateType aggregateType;

    @NotBlank
    private String aggregateId;

    @NotNull
    //    @JsonFormat(pattern = Instants.ISO_INSTANT_FORMAT, timezone = Instants.TIMEZONE)
    private Instant timestamp;

    @NotNull
    private JsonNode payload;

    private String owner;

    // ------------------------------------------------------------------------
    // static factory methods

    public static SchaffbarEvent customerEvent(EventType eventType, CustomerId customerId, CustomerEventPayload payload) {
        return builderWithDefaultsAndNow(eventType) //
                .aggregateType(AggregateType.CUSTOMER) //
                .aggregateId(customerId.getValue().toString()) //
                .payload(toJsonNode(payload)) //
                .build();
    }

    public static SchaffbarEvent toolEvent(EventType eventType, ToolId toolId, ToolEventPayload payload) {
        return builderWithDefaultsAndNow(eventType) //
                .aggregateType(AggregateType.TOOL) //
                .aggregateId(toolId.getValue().toString()) //
                .payload(toJsonNode(payload)) //
                .build();
    }

    public static SchaffbarEvent rfidReaderEvent(EventType eventType, RfidReaderId rfidReaderId, RfidReaderEventPayload payload) {
        return builderWithDefaultsAndNow(eventType) //
                .aggregateType(AggregateType.RFID_READER) //
                .aggregateId(rfidReaderId.getValue().toString()) //
                .payload(toJsonNode(payload)) //
                .build();
    }

    public static SchaffbarEvent rfidTagEvent(EventType eventType, RfidTagId rfidTagId, RfidTagEventPayload payload) {
        return builderWithDefaultsAndNow(eventType) //
                .aggregateType(AggregateType.RFID_TAG) //
                .aggregateId(rfidTagId.getValue()) //
                .payload(toJsonNode(payload)) //
                .build();
    }

    public static SchaffbarEvent rfidTagAssignmentEvent(EventType eventType, RfidTagAssignmentId rfidTagAssignmentId, RfidTagAssignmentEventPayload payload) {
        return builderWithDefaultsAndNow(eventType) //
                .aggregateType(AggregateType.RFID_TAG_ASSIGNMENT) //
                .aggregateId(rfidTagAssignmentId.getValue().toString()) //
                .payload(toJsonNode(payload)) //
                .build();
    }

    public static SchaffbarEvent workshopSessionEvent(EventType eventType, WorkshopSessionId workshopSessionId, WorkshopSessionEventPayload payload) {
        return builderWithDefaultsAndNow(eventType) //
                .aggregateType(AggregateType.WORKSHOP_SESSION) //
                .aggregateId(workshopSessionId.getValue().toString()) //
                .payload(toJsonNode(payload)) //
                .build();
    }

    public static SchaffbarEvent workshopUsageEvent(EventType eventType, WorkshopUsageId workshopUsageId, WorkshopUsageEventPayload payload) {
        return builderWithDefaultsAndNow(eventType) //
                .aggregateType(AggregateType.WORKSHOP_USAGE) //
                .aggregateId(workshopUsageId.getValue().toString()) //
                .payload(toJsonNode(payload)) //
                .build();
    }

    // ------------------------------------------------------------------------
    // serialization

    public String serialize() {
        try {
            return objectMapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e) {
            throw new ValidationException("Unable to serialize afsEvent", e);
        }
    }

    public <T> T deserializeAndValidatePayload(Class<T> classOfT) {
        try {
            T deserialized = objectMapper.treeToValue(this.payload, classOfT);
            validatePayload(deserialized);
            return deserialized;
        }
        catch (JsonProcessingException | IllegalArgumentException | ValidationException e) {
            throw new ValidationException("Unable to de-serialize payload", e);
        }
    }

    // ------------------------------------------------------------------------
    // helper

    private static SchaffbarEventBuilder builderWithDefaultsAndNow(EventType eventType) {
        return builderWithDefaults(eventType, Instant.now());
    }

    private static SchaffbarEventBuilder builderWithDefaults(EventType eventType, Instant creationTimestamp) {
        return builder() //
                .id(UUID.randomUUID()) //
                .type(eventType) //
                .version(EventVersion.V1) //
                .timestamp(creationTimestamp);
    }

    private static JsonNode toJsonNode(EventPayload payload) {
        return objectMapper.valueToTree(payload);
    }

    private static JsonNode toJsonNode(String payload) {
        try {
            return objectMapper.readTree(payload);
        }
        catch (JsonProcessingException e) {
            throw new ValidationException("Unable to de-serialize payload", e);
        }
    }

    // TODO: use method defined in ValidationSupport interface instead of duplicating here
    private <T> void validatePayload(T deserialized) {
        Set<ConstraintViolation<T>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(deserialized);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.toString());
        }
    }

    public enum EventVersion {
        V1
    }

}
