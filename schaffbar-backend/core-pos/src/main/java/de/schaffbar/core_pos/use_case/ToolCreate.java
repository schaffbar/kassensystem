package de.schaffbar.core_pos.use_case;

import static java.util.Objects.nonNull;

import de.schaffbar.core_pos.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.rfid_reader.RfidReaderType;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands;
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
public class ToolCreate {

    private final @NonNull RfidReaderService rfidReaderService;

    private final @NonNull ToolService toolService;

    @Transactional
    public ToolId process(@NotNull @Valid ToolCommands.CreateToolCommand command) {
        if (nonNull(command.rfidReaderId())) {
            RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(command.rfidReaderId()) //
                    .orElseThrow(() -> ResourceNotFoundException.rfidReader(command.rfidReaderId()));

            if (rfidReader.type() != RfidReaderType.SWITCH_BOX) {
                throw new RuntimeException("TODO: Invalid type of RFID reader");
            }
        }

        return this.toolService.createTool(command);
    }

}
