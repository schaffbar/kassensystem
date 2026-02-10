package de.schaffbar.core_pos.rfid_reader;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.rfid_reader.RfidReaderCommands.UpdateRfidReaderCommand;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.MacAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class RfidReaderService {

    private final @NonNull RfidReaderRepository rfidReaderRepository;

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
        // TODO: check if rfidReader with same m<ac address already exists

        RfidReader rfidReader = RfidReader.of(macAddress);
        RfidReader savedRfidReader = this.rfidReaderRepository.save(rfidReader);

        return savedRfidReader.getId();
    }

    @Transactional
    public void updateRfidReader(@NotNull @Valid UpdateRfidReaderCommand command) {
        this.rfidReaderRepository.findById(command.id().getValue()) //
                .ifPresentOrElse( //
                        rfidReader -> rfidReader.update(command), //
                        throwRfidReaderNotFoundException(command.id()));
    }

    @Transactional
    public void deleteRfidReader(@NotNull @Valid RfidReaderId id) {
        RfidReaderView rfidReader = getRfidReader(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        this.rfidReaderRepository.deleteById(rfidReader.id().getValue());
    }

    // ------------------------------------------------------------------------
    // helper

    private Runnable throwRfidReaderNotFoundException(RfidReaderId id) {
        return () -> {
            throw ResourceNotFoundException.rfidReader(id);
        };
    }

}
