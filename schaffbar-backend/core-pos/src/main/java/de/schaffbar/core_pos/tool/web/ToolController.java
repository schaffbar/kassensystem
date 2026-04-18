package de.schaffbar.core_pos.tool.web;

import static java.util.Objects.nonNull;

import java.net.URI;
import java.util.List;

import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateWlanRelaisCommand;
import de.schaffbar.core_pos.tool.ToolService;
import de.schaffbar.core_pos.tool.web.ToolApiModel.CreateToolRequestBody;
import de.schaffbar.core_pos.tool.web.ToolApiModel.ToolApiDto;
import de.schaffbar.core_pos.tool.web.ToolApiModel.UpdateToolRequestBody;
import de.schaffbar.core_pos.tool.web.ToolApiModel.UpdateWlanRelaisRequestBody;
import de.schaffbar.core_pos.use_case.ToolAssignRfidReader;
import de.schaffbar.core_pos.use_case.ToolCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/tools")
public class ToolController {

    private final @NonNull ToolService toolService;

    private final @NonNull ToolCreate toolCreate;

    private final @NonNull ToolAssignRfidReader toolAssignRfidReader;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ToolApiDto>> getAllTools(@RequestParam(required = false) RfidReaderId rfidReaderId) {
        if (nonNull(rfidReaderId)) {
            List<ToolApiDto> assignedTool = this.toolService.getTool(rfidReaderId) //
                    .map(ToolApiModel.MAPPER::toToolApiDto) //
                    .stream().toList();

            return ResponseEntity.ok(assignedTool);
        }

        List<ToolApiDto> tools = this.toolService.getTools().stream() //
                .map(ToolApiModel.MAPPER::toToolApiDto) //
                .toList();

        return ResponseEntity.ok(tools);
    }

    @GetMapping(value = "/{toolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ToolApiDto> getTool(@PathVariable @NotNull @Valid ToolId toolId) {
        ToolApiDto tool = this.toolService.getTool(toolId) //
                .map(ToolApiModel.MAPPER::toToolApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        return ResponseEntity.ok(tool);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createTool(@RequestBody @NotNull @Valid CreateToolRequestBody requestBody) {
        CreateToolCommand command = ToolApiModel.MAPPER.toCreateToolCommand(requestBody);
        ToolId toolId = this.toolCreate.process(command);
        URI location = URI.create("/api/v1/tools/" + toolId.getValue());

        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{toolId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateTool( //
            @PathVariable @NotNull @Valid ToolId toolId, //
            @RequestBody @NotNull @Valid UpdateToolRequestBody requestBody //
    ) {
        UpdateToolCommand command = ToolApiModel.MAPPER.toUpdateToolCommand(requestBody);
        this.toolService.updateTool(toolId, command);

        return ResponseEntity.noContent().build();
    }

    // TODO: streamline with updateWlanRelais either by using a generic update for setting and clearing the WLAN relais and RFID reader
    // or by introducing dedicated endpoints for setting and clearing the WLAN relais
    @PutMapping(value = "/{toolId}/rfid-reader/{rfidReaderId}")
    public ResponseEntity<Void> changeRfidReader( //
            @PathVariable @NotNull @Valid ToolId toolId, //
            @PathVariable @NotNull @Valid RfidReaderId rfidReaderId //
    ) {
        this.toolAssignRfidReader.process(toolId, rfidReaderId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{toolId}/rfid-reader/clear")
    public ResponseEntity<Void> clearRfidReader(@PathVariable @NotNull @Valid ToolId toolId) {
        this.toolService.clearRfidReader(toolId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{toolId}/wlan-relais", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateWlanRelais(@PathVariable @NotNull @Valid ToolId toolId,
            @RequestBody @NotNull @Valid UpdateWlanRelaisRequestBody requestBody) {
        UpdateWlanRelaisCommand command = ToolApiModel.MAPPER.toUpdateWlanRelaisCommand(requestBody);
        this.toolService.updateWlanRelais(toolId, command);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{toolId}")
    public ResponseEntity<Void> deleteTool(@PathVariable @NotNull @Valid ToolId toolId) {
        this.toolService.deleteTool(toolId);

        return ResponseEntity.noContent().build();
    }

}
