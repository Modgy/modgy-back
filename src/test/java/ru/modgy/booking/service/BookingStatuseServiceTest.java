package ru.modgy.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.modgy.booking.model.Booking;
import ru.modgy.booking.model.StatusBooking;
import ru.modgy.booking.model.TypesBooking;
import ru.modgy.owner.model.Owner;
import ru.modgy.pet.model.Pet;
import ru.modgy.pet.model.Sex;
import ru.modgy.pet.model.TypeOfPet;
import ru.modgy.room.category.model.Category;
import ru.modgy.room.model.Room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingStatuseServiceTest {
    private final LocalDate checkIn = LocalDate.of(2024, 1, 1);
    private final LocalDate checkOut = LocalDate.of(2024, 1, 2);
    private final LocalDate checkDate = LocalDate.of(2025, 1,1);
    private final LocalDate futureDate = LocalDate.of(2025, 12, 1);
    private final LocalDateTime registrationDate = LocalDateTime.now();
    private final Owner owner = Owner.builder()
            .id(1L)
            .firstName("Ivan")
            .lastName("Ivanov")
            .middleName("Ivanovich")
            .mainPhone("89000000000")
            .optionalPhone("89000000001")
            .otherContacts("other contacts")
            .actualAddress("actual address")
            .trustedMan("trusted man")
            .source("source")
            .comment("comment")
            .rating(5)
            .registrationDate(registrationDate)
            .build();
    private final Pet pet = Pet.builder()
            .id(1L)
            .owner(owner)
            .type(TypeOfPet.DOG)
            .name("Шарик")
            .breed("Spaniel")
            .birthDate(LocalDate.of(2023, 1, 1))
            .sex(Sex.FEMALE)
            .build();
    private final Room room = Room.builder()
            .id(1L)
            .area(5.0)
            .number("standard room")
            .category(new Category(1L, "name", "description"))
            .isVisible(true)
            .build();
    private final Long bookingId = 1L;
    private final Booking booking = Booking.builder()
            .id(bookingId)
            .type(TypesBooking.TYPE_BOOKING)
            .checkInDate(checkIn)
            .checkOutDate(checkOut)
            .status(StatusBooking.STATUS_INITIAL)
            .price(0.0)
            .amount(0.0)
            .prepaymentAmount(0.0)
            .isPrepaid(false)
            .room(room)
            .pets(List.of(pet))
            .build();

    @InjectMocks
    private BookingStatusService statusService;

    @Test
    void getAllAvailableStatuses_whenBookingInitialStatusAndStartDateAndEndDateInPast_thenReturnedListOfStatuses() {
        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingInitialStatusAndStartDateInPastAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingInitialStatusAndStartDateTodayAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingInitialStatusAndStartDateInPastAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingInitialStatusAndStartDateTodayAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingInitialStatusAndStartDateInFutureAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setCheckInDate(futureDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingConfirmedStatusAndStartDateAndEndDateInPast_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CONFIRMED);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingConfirmedStatusAndStartDateInPastAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CONFIRMED);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingConfirmedStatusAndStartDateTodayAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CONFIRMED);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingConfirmedStatusAndStartDateInPastAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CONFIRMED);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingConfirmedStatusAndStartDateTodayAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CONFIRMED);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingConfirmedStatusAndStartDateInFutureAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CONFIRMED);
        booking.setCheckInDate(futureDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedInStatusAndStartDateAndEndDateInPast_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_IN);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(1));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedInStatusAndStartDateInPastAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_IN);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(2));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(3));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedInStatusAndStartDateTodayAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_IN);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(2));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(3));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedInStatusAndStartDateInPastAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_IN);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(2));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(3));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedInStatusAndStartDateTodayAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_IN);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(2));
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(3));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedInStatusAndStartDateInFutureAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_IN);
        booking.setCheckInDate(futureDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedOutStatusAndStartDateAndEndDateInPast_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_OUT);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(1));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedOutStatusAndStartDateInPastAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_OUT);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(1));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedOutStatusAndStartDateTodayAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_OUT);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CANCELLED, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(1));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedOutStatusAndStartDateInPastAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_OUT);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedOutStatusAndStartDateTodayAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_OUT);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void getAllAvailableStatuses_whenBookingCheckedOutStatusAndStartDateInFutureAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CHECKED_OUT);
        booking.setCheckInDate(futureDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void getAllAvailableStatuses_whenBookingCancelledStatusAndStartDateAndEndDateInPast_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CANCELLED);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(0));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCancelledStatusAndStartDateInPastAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CANCELLED);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(3));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCancelledStatusAndStartDateTodayAndEndDateToday_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CANCELLED);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(checkDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_OUT, result.get(3));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCancelledStatusAndStartDateInPastAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CANCELLED);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCancelledStatusAndStartDateTodayAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CANCELLED);
        booking.setCheckInDate(checkDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
        Assertions.assertEquals(StatusBooking.STATUS_CHECKED_IN, result.get(2));
    }

    @Test
    void getAllAvailableStatuses_whenBookingCancelledStatusAndStartDateInFutureAndEndDateInFuture_thenReturnedListOfStatuses() {
        booking.setStatus(StatusBooking.STATUS_CANCELLED);
        booking.setCheckInDate(futureDate);
        booking.setCheckOutDate(futureDate);

        List<StatusBooking> result = statusService.getAllAvailableStatuses(booking, checkDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(StatusBooking.STATUS_INITIAL, result.get(0));
        Assertions.assertEquals(StatusBooking.STATUS_CONFIRMED, result.get(1));
    }
}
