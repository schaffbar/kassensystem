package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderService;
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
public class RfidReaderDelete {

    private final @NonNull RfidReaderService rfidReaderService;

    @Transactional
    public void process(@NotNull @Valid RfidReaderId rfidReaderId) {
        this.rfidReaderService.deleteRfidReader(rfidReaderId);
    }

}
