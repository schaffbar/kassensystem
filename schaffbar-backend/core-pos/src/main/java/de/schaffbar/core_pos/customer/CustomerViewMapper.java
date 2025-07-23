package de.schaffbar.core_pos.customer;

import de.schaffbar.core_pos.customer.view.CustomerView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerViewMapper {

    CustomerViewMapper MAPPER = Mappers.getMapper(CustomerViewMapper.class);

    @Mapping(target = "id", source = "customerId")
    CustomerView toCustomerView(Customer customer);

}
