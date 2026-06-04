package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderCommands.UpdateRfidReaderCommand;
import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderService;
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
public class RfidReaderUpdate {

    private final @NonNull RfidReaderService rfidReaderService;

    @Transactional
    public void process(@NotNull @Valid UpdateRfidReaderCommand command) {
        this.rfidReaderService.updateRfidReader(command);
    }

}
