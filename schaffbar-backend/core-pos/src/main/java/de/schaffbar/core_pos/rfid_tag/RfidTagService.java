package de.schaffbar.core_pos.rfid_tag;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.rfid_tag.RfidTag.RfidTagWithEvents;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class RfidTagService {

    private final @NonNull RfidTagRepository rfidTagRepository;

    private final @NonNull OutboxEventRepository outboxEventRepository;

    // ------------------------------------------------------------------------
    // query

    public List<RfidTagView> getRfidTags() {
        return this.rfidTagRepository.findAll().stream() //
                .map(RfidTagViews.MAPPER::toRfidTagView) //
                .toList();
    }

    public Optional<RfidTagView> getRfidTag(@NotNull @Valid RfidTagId id) {
        return this.rfidTagRepository.findById(id.getValue()) //
                .map(RfidTagViews.MAPPER::toRfidTagView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public RfidTagId createRfidTag(@NotNull @Valid CreateRfidTagCommand command) {
        // TODO: check if rfidTag with given id already exists

        RfidTagWithEvents result = RfidTag.of(command);
        RfidTag savedRfidTag = this.rfidTagRepository.save(result.rfidTag());

        saveOutboxEvents(result.events());

        log.info("Created RFID tag with id {} and published events {}", savedRfidTag.getId(), result.events());

        return savedRfidTag.getId();
    }

    @Transactional
    public void deleteRfidTag(@NotNull @Valid RfidTagId id) {
        RfidTagView rfidTag = getRfidTag(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(id));

        // TODO: check if RFID tag is assigned to a customer before deleting
        // TODO: maybe force delete in some cases?

        this.rfidTagRepository.deleteById(rfidTag.id().getValue());

        List<SchaffbarEvent> events = List.of(RfidTagEventFactory.rfidTagDeleted(id));
        saveOutboxEvents(events);

        log.info("Deleted RFID tag with id {} and published events {}", id, events);
    }

    // ------------------------------------------------------------------------
    // helper

    // TODO: DRY: this method is duplicated in multiple services, maybe move to a common base class or utility class?
    private void saveOutboxEvents(List<SchaffbarEvent> events) {
        List<OutboxEvent> outboxEvents = events.stream() //
                .map(OutboxEvent::of) //
                .toList();

        this.outboxEventRepository.saveAll(outboxEvents);
    }

}
