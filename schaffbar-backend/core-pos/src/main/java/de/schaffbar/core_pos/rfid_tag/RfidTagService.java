package de.schaffbar.core_pos.rfid_tag;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class RfidTagService {

    private final @NonNull RfidTagRepository rfidTagRepository;

    // ------------------------------------------------------------------------
    // query

    public List<RfidTagView> getRfidTags() {
        return this.rfidTagRepository.findAll().stream() //
                .map(RfidTagViewMapper.MAPPER::toRfidTagView) //
                .toList();
    }

    public Optional<RfidTagView> getRfidTag(@NotNull @Valid RfidTagId id) {
        return this.rfidTagRepository.findById(id.getValue()) //
                .map(RfidTagViewMapper.MAPPER::toRfidTagView);
    }

    // ------------------------------------------------------------------------
    // command

    public RfidTagId createRfidTag(@NotNull @Valid CreateRfidTagCommand command) {
        RfidTag rfidTag = RfidTag.of(command);
        // TODO: check if rfidTag with given id already exists
        RfidTag savedRfidTag = this.rfidTagRepository.save(rfidTag);

        return savedRfidTag.getId();
    }

    public void deleteRfidTag(@NotNull @Valid RfidTagId id) {
        RfidTagView rfidTag = getRfidTag(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(id));

        // TODO: check if RFID tag is assigned to a customer before deleting
        // TODO: maybe force delete in some cases?

        this.rfidTagRepository.deleteById(rfidTag.id().getValue());
    }

}
