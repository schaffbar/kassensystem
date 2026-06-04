package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderService;
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
public class RfidReaderCreate {

    private final @NonNull RfidReaderService rfidReaderService;

    @Transactional
    public RfidReaderId process(@NotNull @Valid MacAddress macAddress) {
        return this.rfidReaderService.createRfidReader(macAddress);
    }

}
