package ru.modgy.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.modgy.booking.dto.BookingDto;
import ru.modgy.booking.dto.NewBookingDto;
import ru.modgy.booking.dto.UpdateBookingDto;
import ru.modgy.booking.dto.mapper.BookingMapper;
import ru.modgy.booking.model.Booking;
import ru.modgy.booking.model.ReasonOfStopBooking;
import ru.modgy.booking.model.StatusBooking;
import ru.modgy.booking.model.TypesBooking;
import ru.modgy.booking.repository.BookingRepository;
import ru.modgy.exception.ConflictException;
import ru.modgy.exception.NotFoundException;
import ru.modgy.owner.dto.mapper.OwnerMapper;
import ru.modgy.owner.model.Owner;
import ru.modgy.pet.dto.PetDto;
import ru.modgy.pet.model.Pet;
import ru.modgy.room.model.Room;
import ru.modgy.utility.EntityService;
import ru.modgy.utility.UtilityService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final OwnerMapper ownerMapper;
    private final EntityService entityService;
    private final UtilityService utilityService;
    private final BookingStatusService statusService;

    @Transactional
    @Override
    public BookingDto addBooking(NewBookingDto newBookingDto) {
        utilityService.checkDatesOfBooking(newBookingDto.getCheckInDate(), newBookingDto.getCheckOutDate());
        checkReasonWhenTypeClosing(newBookingDto.getType(), newBookingDto.getReasonOfStop());
        checkRoomAvailabilityByDates(
                newBookingDto.getRoomId(),
                newBookingDto.getCheckInDate(),
                newBookingDto.getCheckOutDate());

        Booking newBooking = bookingMapper.toBooking(newBookingDto);

        Room room = entityService.getRoomIfExists(newBookingDto.getRoomId());
        newBooking.setRoom(room);
        checkRoom(room, "add");

        if (newBooking.getStatus() == null) {
            if (newBooking.getIsPrepaid() || newBooking.getType().equals(TypesBooking.TYPE_CLOSING)) {
                newBooking.setStatus(StatusBooking.STATUS_CONFIRMED);
            } else {
                newBooking.setStatus(StatusBooking.STATUS_INITIAL);
            }
        }

        List<Pet> pets = entityService.getListOfPetsByIds(newBookingDto.getPetIds());
        checkPetsInBooking(pets, newBookingDto.getPetIds());
        newBooking.setPets(pets);

        Booking addedBooking = bookingRepository.save(newBooking);

        BookingDto bookingDto = bookingMapper.toBookingDto(addedBooking);
        List<PetDto> petDtoList = addPetsDtoListForOwner(pets, bookingDto);

        bookingDto.setPets(petDtoList);
        log.info("BookingService: addBooking, bookingDto={}", addedBooking);
        return bookingDto;
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = entityService.getBookingIfExists(bookingId);
        BookingDto bookingDto = addOwnerShortDtoInPetDto(booking);
        log.info("BookingService: getBookingById, bookingId={}", bookingId);
        return bookingDto;
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long bookingId, LocalDate today, UpdateBookingDto updateBookingDto) {
        Booking oldBooking = entityService.getBookingIfExists(bookingId);
        Booking newBooking = bookingMapper.toBooking(updateBookingDto);
        newBooking.setId(oldBooking.getId());
        newBooking.setType(oldBooking.getType());

        if (Objects.isNull(newBooking.getCheckInDate())) {
            newBooking.setCheckInDate(oldBooking.getCheckInDate());
        }

        if (Objects.isNull(newBooking.getCheckOutDate())) {
            newBooking.setCheckOutDate(oldBooking.getCheckOutDate());
        }

        if (Objects.isNull(newBooking.getCheckInTime())) {
            newBooking.setCheckInTime(oldBooking.getCheckInTime());
        }

        if (Objects.isNull(newBooking.getCheckOutTime())) {
            newBooking.setCheckOutTime(oldBooking.getCheckOutTime());
        }

        if (Objects.isNull(newBooking.getStatus())) {
            newBooking.setStatus(oldBooking.getStatus());
        }

        if (Objects.isNull(newBooking.getReasonOfStop())) {
            newBooking.setReasonOfStop(oldBooking.getReasonOfStop());
        }

        if (Objects.isNull(newBooking.getReasonOfCancel())) {
            newBooking.setReasonOfCancel(oldBooking.getReasonOfCancel());
        }

        if (Objects.isNull(newBooking.getPrice())) {
            newBooking.setPrice(oldBooking.getPrice());
        }

        if (Objects.isNull(newBooking.getAmount())) {
            newBooking.setAmount(oldBooking.getAmount());
        }

        if (Objects.isNull(newBooking.getPrepaymentAmount())) {
            newBooking.setPrepaymentAmount(oldBooking.getPrepaymentAmount());
        }

        if (Objects.isNull(newBooking.getIsPrepaid())) {
            newBooking.setIsPrepaid(oldBooking.getIsPrepaid());
        }

        if (Objects.isNull(newBooking.getComment())) {
            newBooking.setComment(oldBooking.getComment());
        }

        if (Objects.isNull(newBooking.getFileUrl())) {
            newBooking.setFileUrl(oldBooking.getFileUrl());
        }

        if (updateBookingDto.getRoomId() != null) {
            Room room = entityService.getRoomIfExists(updateBookingDto.getRoomId());
            checkRoom(room, "update");
            newBooking.setRoom(room);
        } else {
            newBooking.setRoom(oldBooking.getRoom());
        }

        if (Objects.isNull(updateBookingDto.getPetIds())) {
            newBooking.setPets(oldBooking.getPets());
        } else {
            List<Pet> pets = entityService.getListOfPetsByIds(updateBookingDto.getPetIds());
            checkPetsInBooking(pets, updateBookingDto.getPetIds());
            newBooking.setPets(pets);
        }

        utilityService.checkDatesOfBooking(newBooking.getCheckInDate(), newBooking.getCheckOutDate());
        statusService.checkChangeStatusRestrictions(oldBooking.getStatus(), newBooking, today);
        checkUpdateBookingRoomAvailableByDates(
                newBooking.getRoom().getId(),
                newBooking.getId(),
                newBooking.getCheckInDate(),
                newBooking.getCheckOutDate());
        if (newBooking.getIsPrepaid() && newBooking.getStatus().equals(StatusBooking.STATUS_INITIAL)) {
            newBooking.setStatus(StatusBooking.STATUS_CONFIRMED);
        }

        Booking updatedBooking = bookingRepository.save(newBooking);

        List<Long> petsIds = updatedBooking.getPets().stream().map(Pet::getId).toList();
        List<Pet> pets = entityService.getListOfPetsByIds(petsIds);
        checkPetsInBooking(pets, petsIds);
        updatedBooking.setPets(pets);

        BookingDto updatedBookingDto = bookingMapper.toBookingDto(updatedBooking);
        List<PetDto> petDtoList = addPetsDtoListForOwner(pets, updatedBookingDto);

        updatedBookingDto.setPets(petDtoList);
        log.info("BookingService: updateBooking, bookingId={}, updateBookingDto={}",
               bookingId, updateBookingDto);
        return updatedBookingDto;
    }

    @Transactional
    @Override
    public void deleteBookingById(Long bookingId) {
        int result = bookingRepository.deleteBookingById(bookingId);

        if (result == 0) {
            throw new NotFoundException(String.format("booking with id=%d not found", bookingId));
        }

        log.info("BookingService: deleteBookingById, bookingId={}", bookingId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findCrossingBookingsForRoomInDates(Long roomId,
                                                               LocalDate checkInDate,
                                                               LocalDate checkOutDate) {
        utilityService.checkDatesOfBooking(checkInDate, checkOutDate);
        entityService.getRoomIfExists(roomId);
        List<Booking> foundBookings = bookingRepository.findCrossingBookingsForRoomInDates(
                roomId, checkInDate, checkOutDate).orElse(Collections.emptyList());

        List<BookingDto> bookingDtoList = addOwnerShortDtoInPetDtoList(foundBookings);

        log.info("BookingService: findCrossingBookingsForRoomInDates, roomId={}, checkInDate={}, checkOutDate={}",
                roomId, checkInDate, checkOutDate);
        return bookingDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public void checkRoomAvailableInDates(Long roomId,
                                          LocalDate checkInDate,
                                          LocalDate checkOutDate) {
        utilityService.checkDatesOfBooking(checkInDate, checkOutDate);
        entityService.getRoomIfExists(roomId);
        log.info("BookingService: checkRoomAvailableInDates, roomId={}, checkInDate={}, checkOutDate={}",
                roomId, checkInDate, checkOutDate);
        checkRoomAvailabilityByDates(roomId, checkInDate, checkOutDate);
    }

    @Transactional(readOnly = true)
    @Override
    public void checkUpdateBookingRoomAvailableInDates(Long roomId,
                                                       Long bookingId,
                                                       LocalDate checkInDate,
                                                       LocalDate checkOutDate) {
        utilityService.checkDatesOfBooking(checkInDate, checkOutDate);
        entityService.getBookingIfExists(bookingId);
        entityService.getRoomIfExists(roomId);
        log.info("BookingService: checkUpdateRoomAvailableInDates, roomId={}, checkInDate={}, checkOutDate={}",
                roomId, checkInDate, checkOutDate);
        checkUpdateBookingRoomAvailableByDates(roomId, bookingId, checkInDate, checkOutDate);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findBlockingBookingsForRoomInDates(Long roomId,
                                                               LocalDate checkInDate,
                                                               LocalDate checkOutDate) {
        utilityService.checkDatesOfBooking(checkInDate, checkOutDate);
        entityService.getRoomIfExists(roomId);
        List<Booking> foundBookings = findBookingsForRoomInDates(roomId, checkInDate, checkOutDate);

        List<BookingDto> bookingDtoList = addOwnerShortDtoInPetDtoList(foundBookings);

        log.info("BookingService: findBlockingBookingsForRoomInDates, roomId={}, checkInDate={}, checkOutDate={}",
                roomId, checkInDate, checkOutDate);
        return bookingDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingsInDates(LocalDate startDate, LocalDate endDate) {
        utilityService.checkDatesOfBooking(startDate, endDate);
        List<Booking> foundBookings = bookingRepository.findAllBookingsInDates(startDate, endDate)
                .orElse(Collections.emptyList());

        List<BookingDto> bookingDtoList = addOwnerShortDtoInPetDtoList(foundBookings);

        log.info("BookingService: findAllBookingsInDates, startDate={}, endDate={}",
                startDate, endDate);
        return bookingDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingsByPet(Long petId) {
        entityService.getPetIfExists(petId);
        List<Booking> foundBookings = bookingRepository.findAllBookingsByPet(petId).orElse(Collections.emptyList());
        List<BookingDto> bookingDtoList = addOwnerShortDtoInPetDtoList(foundBookings);

        log.info("BookingService: findAllBookingsByPet, petId={}", petId);
        return bookingDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingsByOwner(Long ownerId) {
        entityService.getOwnerIfExists(ownerId);
        List<Booking> foundBookings = bookingRepository.findAllBookingsByOwner(ownerId).orElse(Collections.emptyList());
        List<BookingDto> bookingDtoList = addOwnerShortDtoInPetDtoList(foundBookings);

        log.info("BookingService: findAllBookingsByOwner, petId={}", ownerId);
        return bookingDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatusBooking> getAllAvailableStatuses(Long bookingId, LocalDate date) {
        Booking booking = entityService.getBookingIfExists(bookingId);

        log.info("BookingService: getAllAvailableStatuses, bookingId={}, date={}", bookingId, date);
        return statusService.getAllAvailableStatuses(booking, date);
    }

    private List<Booking> findBookingsForRoomInDates(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return bookingRepository.findBookingsForRoomInDates(
                        roomId, checkInDate, checkOutDate)
                .orElse(Collections.emptyList());
    }

    private void checkRoomAvailabilityByDates(Long roomId,
                                              LocalDate checkInDate,
                                              LocalDate checkOutDate) {
        List<Booking> blockingBookings = findBookingsForRoomInDates(roomId, checkInDate, checkOutDate);
        if (!blockingBookings.isEmpty()) {
            throw new ConflictException(String.format("Room with id=%d is not available for current dates", roomId));
        }
    }

    private void checkUpdateBookingRoomAvailableByDates(Long roomId,
                                                        Long bookingId,
                                                        LocalDate checkInDate,
                                                        LocalDate checkOutDate) {
        List<Booking> filteredBookings = findBookingsForRoomInDates(roomId, checkInDate, checkOutDate)
                .stream()
                .filter(booking -> !booking.getId().equals(bookingId))
                .toList();

        if (!filteredBookings.isEmpty()) {
            throw new ConflictException(String.format("Room with id=%d is not available for current dates", roomId));
        }
    }

    private void checkReasonWhenTypeClosing(TypesBooking type, ReasonOfStopBooking reason) {
        if (type.equals(TypesBooking.TYPE_CLOSING)) {
            if (reason == null) {
                throw new ConflictException("Reason of stop booking cannot be null when Type Booking is CLOSING");
            }
        }
    }

    private void checkPetsInBooking(List<Pet> pets, List<Long> petIds) {
        for (Long id : petIds) {
            boolean found = false;
            for (Pet pet : pets) {
                if (pet.getId() == id) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new NotFoundException(String.format("Pet with id=%d is not found", id));
            }
        }
    }

    private void checkRoom(Room room, String actionType) {
        if (!room.getIsVisible()) {
            throw new ConflictException("Can't " + actionType + " booking for hidden room");
        }
    }

    private BookingDto addOwnerShortDtoInPetDto(Booking booking) {
        List<Long> petsIds = booking.getPets().stream().map(Pet::getId).toList();
        List<Pet> pets = entityService.getListOfPetsByIds(petsIds);
        checkPetsInBooking(pets, petsIds);

        Map<Long, Owner> owners = pets.stream()
                .collect(Collectors.toMap(Pet::getId, Pet::getOwner));

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);
        List<PetDto> petsDto = bookingDto.getPets();
        for (PetDto petDto : petsDto) {
            petDto.setOwnerShortDto(ownerMapper.toOwnerShortDto(owners.get(petDto.getId())));
        }
        bookingDto.setPets(petsDto);
        return bookingDto;
    }

    private List<BookingDto> addOwnerShortDtoInPetDtoList(List<Booking> bookings) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingDto bookingDto = addOwnerShortDtoInPetDto(booking);
            bookingDtoList.add(bookingDto);
        }
        return bookingDtoList;
    }

    private List<PetDto> addPetsDtoListForOwner(List<Pet> pets, BookingDto bookingDto) {
        Map<Long, Owner> owners = pets.stream()
                .collect(Collectors.toMap(Pet::getId, Pet::getOwner));

        List<PetDto> petsDto = bookingDto.getPets();
        for (PetDto petDto : petsDto) {
            petDto.setOwnerShortDto(ownerMapper.toOwnerShortDto(owners.get(petDto.getId())));
        }
        return petsDto;
    }
}
