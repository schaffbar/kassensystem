package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderCommands.ChangeRfidReaderTypeCommand;
import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderType;
import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.exception.RfidReaderHasToolAssignedException;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.domain.tool.ToolService;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class RfidReaderChangeType {

    private final @NonNull RfidReaderService rfidReaderService;

    private final @NonNull ToolService toolService;

    @Transactional
    public void process(@NotNull @Valid ChangeRfidReaderTypeCommand command) {
        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(command.id()) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(command.id()));

        if (rfidReader.type() == RfidReaderType.SWITCH_BOX) {
            verifyNoToolAssigned(command.id());
        }

        this.rfidReaderService.changeRfidReaderType(command);
    }

    // ------------------------------------------------------------------------
    // helper

    private void verifyNoToolAssigned(RfidReaderId rfidReaderId) {
        this.toolService.getTool(rfidReaderId) //
                .ifPresent(_ -> {
                    throw new RfidReaderHasToolAssignedException(rfidReaderId);
                });
    }

}
