package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/moment")
public class MomentController {
    private final MomentService momentService;
    private final MomentMapper momentMapper;

    @PostMapping
    public ResponseEntity<MomentDto> create(@RequestBody @Valid MomentDto momentDto) {
        Moment newMoment = momentMapper.toEntity(momentDto);
        Moment savedMoment = momentService.save(newMoment);
        MomentDto responseDto = momentMapper.toDto(savedMoment);

        return ResponseEntity
                .created(URI.create("/moment/" + savedMoment.getId()))
                .body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MomentDto> findById(@PathVariable Long id) {
        return momentService.findById(id)
                .map(momentMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<MomentDto> findAll(@ModelAttribute MomentFilterDto filter) {
        List<Moment> moments = momentService.findAll(filter);

        return moments.stream()
                .map(momentMapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        momentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public MomentDto updateMoment(@PathVariable Long id, @RequestBody MomentDto momentDto) {

        return momentMapper.toDto(momentService.updateMoment(id, momentDto));
    }
}
