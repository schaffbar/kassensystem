package de.schaffbar.core_pos.rfid_tag;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.RfidTagId;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
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

    public Optional<RfidTagView> getRfidTag(@NotNull @Valid String tagId) {
        return this.rfidTagRepository.findByTagId(tagId) //
                .map(RfidTagViewMapper.MAPPER::toRfidTagView);
    }

    // ------------------------------------------------------------------------
    // command

    public RfidTagId createRfidTag(@NotNull @Valid CreateRfidTagCommand command) {
        RfidTag rfidTag = RfidTag.of(command);
        RfidTag savedRfidTag = this.rfidTagRepository.save(rfidTag);

        return savedRfidTag.getId();
    }

    public void deleteRfidTag(@NotNull @Valid RfidTagId id) {
        RfidTagView rfidTag = getRfidTag(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(id));

        this.rfidTagRepository.deleteById(rfidTag.id().getValue());
    }

}
