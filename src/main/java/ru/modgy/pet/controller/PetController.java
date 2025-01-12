package ru.modgy.pet.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.modgy.pet.dto.NewPetDto;
import ru.modgy.pet.dto.PetDto;
import ru.modgy.pet.dto.UpdatePetDto;
import ru.modgy.pet.service.PetService;
import ru.modgy.utility.UtilityService;


@CrossOrigin
@RestController
@RequestMapping(path = "/pets")
@RequiredArgsConstructor
@Slf4j
public class PetController {
    private final PetService petService;
    private final UtilityService utilityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetDto addPet(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                         @RequestBody @Valid NewPetDto newPetDto) {
        utilityService.checkBossAdminAccess(requesterId);
        log.info("PetController: POST/addPet, requesterId={}", requesterId);
        return petService.addPet(requesterId, newPetDto);
    }

    @GetMapping("/{id}")
    public PetDto getPetById(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                             @PathVariable(value = "id") Long petId) {
        log.info("PetController: GET/getPetById, requesterId={}, petId={}", requesterId, petId);
        return petService.getPetById(requesterId, petId);
    }

    @GetMapping("/search")
    public Page<PetDto> getPetsBySearch(
            @RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
            @RequestParam(required = false) String text,
            @PositiveOrZero
            @RequestParam(defaultValue = "0") Integer page,
            @Positive
            @RequestParam(defaultValue = "10") Integer size) {
        utilityService.checkBossAdminAccess(requesterId);
        log.info("PetController: GET/getPetsBySearch, requesterId={}, text={}, page={}, size={}", requesterId,
                text, page, size);
        return petService.getPetsBySearch(
                requesterId,
                text,
                page,
                size);
    }

    @PatchMapping("/{id}")
    public PetDto updatePet(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                            @RequestBody @Valid UpdatePetDto updatePetDto,
                            @PathVariable(value = "id") Long petId) {
        utilityService.checkBossAdminAccess(requesterId);
        log.info("PetController: PATCH/updatePet, requesterId={}, petId={}", requesterId, petId);
        return petService.updatePet(requesterId, petId, updatePetDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePetById(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                              @PathVariable(value = "id") Long petId) {
        utilityService.checkBossAdminAccess(requesterId);
        log.info("PetController: DELETE/deletePetById, requesterId= {}, petId={}", requesterId, petId);
        petService.deletePetById(requesterId, petId);
    }

}