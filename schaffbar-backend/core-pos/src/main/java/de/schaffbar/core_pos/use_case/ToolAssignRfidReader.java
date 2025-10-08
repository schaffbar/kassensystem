package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.id.RfidReaderId;
import de.schaffbar.core_pos.id.ToolId;
import de.schaffbar.core_pos.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.rfid_reader.RfidReaderType;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.tool.ToolService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ToolAssignRfidReader {

    private final @NonNull RfidReaderService rfidReaderService;

    private final @NonNull ToolService toolService;

    @Transactional
    public void process(@NotNull @Valid ToolId toolId, @NotNull @Valid RfidReaderId rfidReaderId) {
        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(rfidReaderId) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(rfidReaderId));

        if (rfidReader.type() != RfidReaderType.SWITCH_BOX) {
            throw new RuntimeException("TODO: Invalid type of RFID reader");
        }

        this.toolService.assignRfidReader(toolId, rfidReaderId);
    }

}
