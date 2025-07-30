package de.schaffbar.core_pos.rfid_reader;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.RfidReaderId;
import de.schaffbar.core_pos.rfid_reader.RfidReaderCommands.CreateRfidReaderCommand;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                .map(RfidReaderViewMapper.MAPPER::toRfidReaderView) //
                .toList();
    }

    public Optional<RfidReaderView> getRfidReader(@NotNull @Valid RfidReaderId id) {
        return this.rfidReaderRepository.findById(id.getValue()) //
                .map(RfidReaderViewMapper.MAPPER::toRfidReaderView);
    }

    // ------------------------------------------------------------------------
    // command

    public RfidReaderId createRfidReader(@NotNull @Valid CreateRfidReaderCommand command) {
        RfidReader rfidReader = RfidReader.of(command);
        RfidReader savedRfidReader = this.rfidReaderRepository.save(rfidReader);

        return savedRfidReader.getId();
    }

    public void deleteRfidReader(@NotNull @Valid RfidReaderId id) {
        RfidReaderView rfidReader = getRfidReader(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        this.rfidReaderRepository.deleteById(rfidReader.id().getValue());
    }

}
