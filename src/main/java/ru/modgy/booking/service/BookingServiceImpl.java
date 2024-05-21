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
import ru.modgy.pet.model.Pet;
import ru.modgy.room.model.Room;
import ru.modgy.utility.EntityService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EntityService entityService;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, NewBookingDto newBookingDto) {
        checkDates(newBookingDto.getCheckInDate(), newBookingDto.getCheckOutDate());
        checkReasonWhenTypeClosing(newBookingDto.getType(), newBookingDto.getReasonOfStop());
        checkRoomAvailabilityByDates(
                newBookingDto.getRoomId(),
                newBookingDto.getCheckInDate(),
                newBookingDto.getCheckOutDate());

        Booking newBooking = bookingMapper.toBooking(newBookingDto);

        Room room = entityService.getRoomIfExists(newBookingDto.getRoomId());
        newBooking.setRoom(room);

        if (newBooking.getStatus() == null) {
            if (newBooking.getIsPrepaid() | newBooking.getType().equals(TypesBooking.TYPE_CLOSING)) {
                newBooking.setStatus(StatusBooking.STATUS_CONFIRMED);
            } else {
                newBooking.setStatus(StatusBooking.STATUS_INITIAL);
            }
        }

        List<Pet> pets = entityService.getListOfPetsByIds(newBookingDto.getPetIds());
        checkPetsInBooking(pets, newBookingDto.getPetIds());
        newBooking.setPets(pets);

        Booking addedBooking = bookingRepository.save(newBooking);
        log.info("BookingService: addBooking, userId={}, bookingDto={}", userId, addedBooking);

        return bookingMapper.toBookingDto(addedBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = entityService.getBookingIfExists(bookingId);
        log.info("BookingService: getBookingById, userId={}, bookingId={}", userId, bookingId);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, UpdateBookingDto updateBookingDto) {
        Booking oldBooking = entityService.getBookingIfExists(bookingId);
        Booking newBooking = bookingMapper.toBooking(updateBookingDto);
        newBooking.setId(oldBooking.getId());
        newBooking.setType(oldBooking.getType());

        if (Objects.isNull(newBooking.getCheckInDate())) {
            newBooking.setCheckInDate(oldBooking.getCheckInDate());
        } else {
            checkRoomAvailabilityByDates(
                    updateBookingDto.getRoomId(),
                    updateBookingDto.getCheckInDate(),
                    updateBookingDto.getCheckOutDate());
        }

        if (Objects.isNull(newBooking.getCheckOutDate())) {
            newBooking.setCheckOutDate(oldBooking.getCheckOutDate());
        } else {
            checkRoomAvailabilityByDates(
                    updateBookingDto.getRoomId(),
                    updateBookingDto.getCheckInDate(),
                    updateBookingDto.getCheckOutDate());
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
            newBooking.setRoom(entityService.getRoomIfExists(updateBookingDto.getRoomId()));
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

        if (newBooking.getStatus().equals(StatusBooking.STATUS_INITIAL) & newBooking.getIsPrepaid()) {
            newBooking.setStatus(StatusBooking.STATUS_CONFIRMED);
        }

        checkDates(newBooking.getCheckInDate(), newBooking.getCheckOutDate());

        Booking updatedBooking = bookingRepository.save(newBooking);
        BookingDto updatedBookingDto = bookingMapper.toBookingDto(updatedBooking);
        log.info("BookingService: updateBooking, userId={}, bookingId={}, updateBookingDto={}",
                userId, bookingId, updateBookingDto);

        return updatedBookingDto;
    }

    @Transactional
    @Override
    public void deleteBookingById(Long userId, Long bookingId) {
        int result = bookingRepository.deleteBookingById(bookingId);

        if (result == 0) {
            throw new NotFoundException(String.format("booking with id=%d not found", bookingId));
        }

        log.info("BookingService: deleteBookingById, userId={}, bookingId={}", userId, bookingId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findCrossingBookingsForRoomInDates(Long userId,
                                                       Long roomId,
                                                       LocalDate checkInDate,
                                                       LocalDate checkOutDate) {
        checkAdminAccess(userId);

        List<Booking> foundBookings = bookingRepository.findCrossingBookingsForRoomInDates(
                        roomId, checkInDate, checkOutDate).orElse(Collections.emptyList());

        log.info("BookingService: findBookingsForRoomInDates, userId={}, roomId={}, checkInDate={}, checkOutDate={}",
                userId, roomId, checkInDate, checkOutDate);
        return bookingMapper.toBookingDto(foundBookings);
    }

    @Transactional(readOnly = true)
    @Override
    public void checkRoomAvailableInDates(Long userId,
                                          Long roomId,
                                          LocalDate checkInDate,
                                          LocalDate checkOutDate) {
        checkAdminAccess(userId);
        log.info("BookingService: checkRoomAvailableInDates, userId={}, roomId={}, checkInDate={}, checkOutDate={}",
                userId, roomId, checkInDate, checkOutDate);
        checkRoomAvailabilityByDates(roomId, checkInDate, checkOutDate);
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
        if(!blockingBookings.isEmpty()) {
            throw new ConflictException(String.format("Room with id=%d is not available for current dates", roomId));
        }
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("booking with id=%d is not found", id)));
    }

    private Room findRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("room with id=%d is not found", id)));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id=%d is not found", userId)));
    }

    private void checkAdminAccess(Long userId) {
        User user = findUserById(userId);

        if (user.getRole().ordinal() >= 2) {
            throw new AccessDeniedException(String.format("User with role=%s, can't access for this action",
                    user.getRole()));
        }
    }

    private void checkDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isAfter(checkOutDate)) {
            throw new ConflictException(String.format("CheckInDate=%s is after CheckOutDate=%s",
                    checkInDate, checkOutDate));
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

    private void checkRoom(Room room) {
        if (!room.getIsVisible()) {
            throw new ConflictException("Can't add booking for hidden room");
        }
    }
}
