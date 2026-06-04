package de.schaffbar.core_pos.domain.rfid_reader;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderCommands.ChangeRfidReaderTypeCommand;
import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderCommands.UpdateRfidReaderCommand;
import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.MacAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
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
public class RfidReaderService {

    private final @NonNull RfidReaderRepository rfidReaderRepository;

    private final @NonNull OutboxEventRepository outboxEventRepository;

    // ------------------------------------------------------------------------
    // query

    public List<RfidReaderView> getRfidReaders() {
        return this.rfidReaderRepository.findAll().stream() //
                .map(RfidReaderViews.MAPPER::toRfidReaderView) //
                .toList();
    }

    public Optional<RfidReaderView> getRfidReader(@NotNull @Valid RfidReaderId id) {
        return this.rfidReaderRepository.findById(id.getValue()) //
                .map(RfidReaderViews.MAPPER::toRfidReaderView);
    }

    public Optional<RfidReaderView> getRfidReader(@NotNull @Valid MacAddress macAddress) {
        return this.rfidReaderRepository.findByMacAddress(macAddress.getValue()) //
                .map(RfidReaderViews.MAPPER::toRfidReaderView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public RfidReaderId createRfidReader(@NotNull @Valid MacAddress macAddress) {
        // TODO: check if rfidReader with same mac address already exists

        RfidReader rfidReader = RfidReader.of(macAddress);
        RfidReader savedRfidReader = this.rfidReaderRepository.save(rfidReader);
        List<SchaffbarEvent> events = List.of(RfidReaderEventFactory.rfidReaderCreated(savedRfidReader));

        saveOutboxEvents(events);

        log.info("Created RFID reader with id {} and published events {}", savedRfidReader.getId(), events);

        return savedRfidReader.getId();
    }

    @Transactional
    public void updateRfidReader(@NotNull @Valid UpdateRfidReaderCommand command) {
        List<SchaffbarEvent> events = this.rfidReaderRepository.findById(command.id().getValue()) //
                .map(rfidReader -> rfidReader.update(command)) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(command.id()));

        saveOutboxEvents(events);

        log.info("Updated RFID reader with id {} and published events {}", command.id(), events);
    }

    @Transactional
    public void changeRfidReaderType(@NotNull @Valid ChangeRfidReaderTypeCommand command) {
        List<SchaffbarEvent> events = this.rfidReaderRepository.findById(command.id().getValue()) //
                .map(rfidReader -> rfidReader.changeType(command)) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(command.id()));

        saveOutboxEvents(events);

        log.info("Updated type of RFID reader with id {} and published events {}", command.id(), events);
    }

    @Transactional
    public void deleteRfidReader(@NotNull @Valid RfidReaderId id) {
        RfidReaderView rfidReader = getRfidReader(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        this.rfidReaderRepository.deleteById(rfidReader.id().getValue());

        List<SchaffbarEvent> events = List.of(RfidReaderEventFactory.rfidReaderDeleted(id));
        saveOutboxEvents(events);

        log.info("Deleted RFID reader with id {} and published events {}", id, events);
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
