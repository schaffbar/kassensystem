package de.schaffbar.core_pos.domain.tool_certification.web;

import static java.util.Objects.nonNull;

import java.util.List;

import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolCertificationId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.domain.tool_certification.BatchCertificationResult;
import de.schaffbar.core_pos.domain.tool_certification.ToolCertificationService;
import de.schaffbar.core_pos.domain.tool_certification.web.ToolCertificationApiModel.BatchCertifyRequestBody;
import de.schaffbar.core_pos.domain.tool_certification.web.ToolCertificationApiModel.ToolCertificationApiDto;
import de.schaffbar.core_pos.use_case.CertifyCustomersForTool;
import de.schaffbar.core_pos.use_case.DeleteToolCertification;
import de.schaffbar.core_pos.use_case.PauseToolCertification;
import de.schaffbar.core_pos.use_case.ReactivateToolCertification;
import de.schaffbar.core_pos.use_case.RevokeToolCertification;
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
@RequestMapping(path = "/api/v1/tool-certifications")
public class ToolCertificationController {

    private final @NonNull ToolCertificationService toolCertificationService;

    private final @NonNull CertifyCustomersForTool certifyCustomersForTool;

    private final @NonNull PauseToolCertification pauseToolCertification;

    private final @NonNull ReactivateToolCertification reactivateToolCertification;

    private final @NonNull RevokeToolCertification revokeToolCertification;

    private final @NonNull DeleteToolCertification deleteToolCertification;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(value = "/{certificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ToolCertificationApiDto> getCertification(@PathVariable @NotNull @Valid ToolCertificationId certificationId) {
        ToolCertificationApiDto dto = this.toolCertificationService.getCertification(certificationId) //
                .map(ToolCertificationApiModel.MAPPER::toApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.toolCertification(certificationId));

        return ResponseEntity.ok(dto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ToolCertificationApiDto>> getCertifications(@RequestParam(required = false) CustomerId customerId,
            @RequestParam(required = false) ToolId toolId) {

        List<ToolCertificationApiDto> certifications;

        if (nonNull(customerId)) {
            certifications = this.toolCertificationService.getCertifications(customerId).stream() //
                    .map(ToolCertificationApiModel.MAPPER::toApiDto) //
                    .toList();
        }
        else if (nonNull(toolId)) {
            certifications = this.toolCertificationService.getCertifications(toolId).stream() //
                    .map(ToolCertificationApiModel.MAPPER::toApiDto) //
                    .toList();
        }
        else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(certifications);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(value = "/batch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BatchCertificationResult> batchCertify(@RequestBody @Valid BatchCertifyRequestBody requestBody) {
        BatchCertificationResult result = this.certifyCustomersForTool.process(requestBody.customerIds(), requestBody.toolId(), requestBody.certifiedBy());

        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/customers/{customerId}/tools/{toolId}/pause")
    public ResponseEntity<Void> pause(@PathVariable @NotNull @Valid CustomerId customerId, @PathVariable @NotNull @Valid ToolId toolId) {
        this.pauseToolCertification.process(customerId, toolId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/customers/{customerId}/tools/{toolId}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable @NotNull @Valid CustomerId customerId, @PathVariable @NotNull @Valid ToolId toolId) {
        this.reactivateToolCertification.process(customerId, toolId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/customers/{customerId}/tools/{toolId}/revoke")
    public ResponseEntity<Void> revoke(@PathVariable @NotNull @Valid CustomerId customerId, @PathVariable @NotNull @Valid ToolId toolId) {
        this.revokeToolCertification.process(customerId, toolId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/customers/{customerId}/tools/{toolId}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull @Valid CustomerId customerId, @PathVariable @NotNull @Valid ToolId toolId) {
        this.deleteToolCertification.process(customerId, toolId);

        return ResponseEntity.noContent().build();
    }

}
