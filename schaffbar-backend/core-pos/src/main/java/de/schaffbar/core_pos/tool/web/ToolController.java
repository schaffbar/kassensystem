package de.schaffbar.core_pos.tool.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolService;
import de.schaffbar.core_pos.tool.web.ToolApiModel.CreateToolRequestBody;
import de.schaffbar.core_pos.tool.web.ToolApiModel.ToolApiDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/tools")
public class ToolController {

    private final @NonNull ToolService toolService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ToolApiDto>> getAllTools() {
        List<ToolApiDto> tools = this.toolService.getTools().stream() //
                .map(ToolApiMapper.MAPPER::toToolApiDto) //
                .toList();

        return ResponseEntity.ok(tools);
    }

    @GetMapping(value = "/{toolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ToolApiDto> getTool(@PathVariable @NotNull UUID toolId) {
        ToolId id = ToolId.of(toolId);
        ToolApiDto tool = this.toolService.getTool(id) //
                .map(ToolApiMapper.MAPPER::toToolApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.tool(id));

        return ResponseEntity.ok(tool);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createTool(@RequestBody @NotNull @Valid CreateToolRequestBody requestBody) {
        CreateToolCommand command = ToolApiMapper.MAPPER.toCreateToolCommand(requestBody);
        ToolId toolId = this.toolService.createTool(command);
        URI location = URI.create("/api/v1/tools/" + toolId.getValue());

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{toolId}")
    public ResponseEntity<Void> deleteTool(@PathVariable @NotNull UUID toolId) {
        ToolId id = ToolId.of(toolId);
        this.toolService.deleteTool(id);

        return ResponseEntity.noContent().build();
    }

}
