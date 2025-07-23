package de.schaffbar.core_pos.customer.view;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class CustomerAddressView {

    @NotBlank
    String addressLine1;

    String addressLine2;

    @NotBlank
    String postalCode;

    @NotBlank
    String city;

    @NotBlank
    String country;

}
