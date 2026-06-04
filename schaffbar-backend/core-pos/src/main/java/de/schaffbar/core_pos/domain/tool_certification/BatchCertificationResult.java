package de.schaffbar.core_pos.domain.tool_certification;

import java.util.List;

import de.schaffbar.core_pos.shared.id.CustomerId;

public record BatchCertificationResult( //
        List<CustomerId> succeeded, //
        List<BatchCertificationError> failed //
) {

    public record BatchCertificationError( //
            CustomerId customerId, //
            String reason //
    ) {}

}
