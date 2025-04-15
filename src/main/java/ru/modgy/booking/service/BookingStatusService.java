package ru.modgy.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.modgy.booking.model.Booking;
import ru.modgy.booking.model.StatusBooking;
import ru.modgy.exception.BadRequestException;
import ru.modgy.exception.ConflictException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingStatusService {

    public List<StatusBooking> getAllAvailableStatuses(Booking booking, LocalDate date) {
        if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL)) {
            return getStatusesForInitialBooking(booking, date);
        } else if (booking.getStatus().equals(StatusBooking.STATUS_CONFIRMED)) {
            return getStatusesForConfirmedBooking(booking, date);
        } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_IN)) {
            return getStatusesForCheckedInBooking(booking, date);
        } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
            return getStatusesForCheckedOutBooking(booking, date);
        } else {
            return getStatusesForCancelledBooking(booking, date);
        }
    }

    public void checkChangeStatusRestrictions(StatusBooking previousStatus, Booking booking, LocalDate today) {
        if (previousStatus.equals(StatusBooking.STATUS_CANCELLED)) {
            checkCancelledStatusRestrictions(booking, today);
        }

        if (previousStatus.equals(StatusBooking.STATUS_INITIAL)) {
            checkInitialStatusRestrictions(booking, today);
        }

        if (previousStatus.equals(StatusBooking.STATUS_CONFIRMED)) {
            checkConfirmedStatusRestrictions(booking, today);
        }

        if (previousStatus.equals(StatusBooking.STATUS_CHECKED_IN)) {
            checkCheckedInStatusRestrictions(booking, today);
        }

        if (previousStatus.equals(StatusBooking.STATUS_CHECKED_OUT)) {
            checkCheckedOutStatusRestrictions(booking, today);
        }
    }

    private List<StatusBooking> getStatusesForInitialBooking(Booking booking, LocalDate date) {
        List<StatusBooking> result = new ArrayList<>();
        result.add(StatusBooking.STATUS_CONFIRMED);
        result.add(StatusBooking.STATUS_CANCELLED);

        if (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isBefore(date)) {
            result.add(StatusBooking.STATUS_CHECKED_OUT);
        } else if ((booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isAfter(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isAfter(date)) ||
                (booking.getCheckInDate().isAfter(date) && booking.getCheckOutDate().isAfter(date))) {
            result.add(StatusBooking.STATUS_CHECKED_IN);
        }
        return result;
    }

    private List<StatusBooking> getStatusesForConfirmedBooking(Booking booking, LocalDate date) {
        List<StatusBooking> result = new ArrayList<>();
        result.add(StatusBooking.STATUS_INITIAL);
        result.add(StatusBooking.STATUS_CANCELLED);

        if (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isBefore(date)) {
            result.add(StatusBooking.STATUS_CHECKED_OUT);
        } else if ((booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isAfter(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isAfter(date)) ||
                (booking.getCheckInDate().isAfter(date) && booking.getCheckOutDate().isAfter(date))) {
            result.add(StatusBooking.STATUS_CHECKED_IN);
        }
        return result;
    }

    private List<StatusBooking> getStatusesForCheckedInBooking(Booking booking, LocalDate date) {
        List<StatusBooking> result = new ArrayList<>();

        if (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isBefore(date)) {
            result.add(StatusBooking.STATUS_CHECKED_OUT);
            result.add(StatusBooking.STATUS_CANCELLED);
        } else if ((booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isAfter(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isAfter(date))) {
            result.add(StatusBooking.STATUS_INITIAL);
            result.add(StatusBooking.STATUS_CONFIRMED);
            result.add(StatusBooking.STATUS_CHECKED_OUT);
            result.add(StatusBooking.STATUS_CANCELLED);
        }
        return result;
    }

    private List<StatusBooking> getStatusesForCheckedOutBooking(Booking booking, LocalDate date) {
        List<StatusBooking> result = new ArrayList<>();

        if ((booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isBefore(date)) ||
                (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isEqual(date))) {
            result.add(StatusBooking.STATUS_CANCELLED);
            result.add(StatusBooking.STATUS_CHECKED_IN);
        }
        return result;
    }

    private List<StatusBooking> getStatusesForCancelledBooking(Booking booking, LocalDate date) {
        List<StatusBooking> result = new ArrayList<>();

        if (booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isBefore(date)) {
            result.add(StatusBooking.STATUS_CHECKED_OUT);
        } else if ((booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isEqual(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isEqual(date))) {
            result.add(StatusBooking.STATUS_INITIAL);
            result.add(StatusBooking.STATUS_CONFIRMED);
            result.add(StatusBooking.STATUS_CHECKED_IN);
            result.add(StatusBooking.STATUS_CHECKED_OUT);
        } else if (booking.getCheckInDate().isAfter(date) && booking.getCheckOutDate().isAfter(date)) {
            result.add(StatusBooking.STATUS_INITIAL);
            result.add(StatusBooking.STATUS_CONFIRMED);
        } else if ((booking.getCheckInDate().isBefore(date) && booking.getCheckOutDate().isAfter(date)) ||
                (booking.getCheckInDate().isEqual(date) && booking.getCheckOutDate().isAfter(date))) {
            result.add(StatusBooking.STATUS_INITIAL);
            result.add(StatusBooking.STATUS_CONFIRMED);
            result.add(StatusBooking.STATUS_CHECKED_IN);
        }
        return result;
    }

    private void checkCancelledStatusRestrictions(Booking booking, LocalDate today) {
        if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isBefore(today) &&
                !booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
            throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
        }

        if ((booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isEqual(today)) ||
                (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isEqual(today))) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            }
        }

        if ((booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isAfter(today)) ||
                (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isAfter(today))) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }

        if (booking.getCheckInDate().isAfter(today) && booking.getCheckOutDate().isAfter(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT) || booking.getStatus().equals(StatusBooking.STATUS_CHECKED_IN)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }
    }

    private void checkInitialStatusRestrictions(Booking booking, LocalDate today) {
        if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isBefore(today) &&
                booking.getStatus().equals(StatusBooking.STATUS_CHECKED_IN)) {
            throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
        }

        if (((booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isEqual(today)) ||
                (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isEqual(today))) &&
                booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
            throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
        }

        if ((booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isAfter(today)) &&
                booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
            throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
        }

        if ((booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isAfter(today)) &&
                booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
            throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
        }

        if (booking.getCheckInDate().isAfter(today) && booking.getCheckOutDate().isAfter(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_IN)) {
                throw new ConflictException(String.format("CheckInDate=%s is after today=%s",
                        booking.getCheckInDate(), today));
            }
        }
    }

    private void checkConfirmedStatusRestrictions(Booking booking, LocalDate today) {
        if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isBefore(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_IN)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }

        if ((booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isEqual(today)) ||
                (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isEqual(today))) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }

        if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isAfter(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }

        if (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isAfter(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }

        if (booking.getCheckInDate().isAfter(today) && booking.getCheckOutDate().isAfter(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_IN)) {
                throw new ConflictException(String.format("CheckInDate=%s is after today=%s",
                        booking.getCheckInDate(), today));
            }
        }
    }

    private void checkCheckedInStatusRestrictions(Booking booking, LocalDate today) {
        if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isBefore(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) ||
                    booking.getStatus().equals(StatusBooking.STATUS_CONFIRMED)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }

        if ((booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isEqual(today)) ||
                (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isEqual(today))) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            }
        }

        if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isAfter(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("CheckOutDate=%s is after today=%s",
                        booking.getCheckOutDate(), today));
            }
        }

        if (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isAfter(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) && booking.getIsPrepaid()) {
                throw new BadRequestException("Prepaid booking can not be INITIAL. Set isPrepaid=false or set STATUS_CONFIRMED.");
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_OUT)) {
                throw new BadRequestException(String.format("CheckOutDate=%s is after today=%s",
                        booking.getCheckOutDate(), today));
            }
        }

        if (booking.getCheckInDate().isAfter(today) && booking.getCheckOutDate().isAfter(today)) {
            throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
        }
    }

    private void checkCheckedOutStatusRestrictions(Booking booking, LocalDate today) {
        if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isBefore(today)) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) || booking.getStatus().equals(StatusBooking.STATUS_CONFIRMED)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            } else if (booking.getStatus().equals(StatusBooking.STATUS_CHECKED_IN)) {
                throw new BadRequestException(String.format("CheckOutDate=%s is before today=%s",
                        booking.getCheckOutDate(), today));
            }
        }

        if ((booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isEqual(today)) ||
                (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isEqual(today))) {
            if (booking.getStatus().equals(StatusBooking.STATUS_INITIAL) || booking.getStatus().equals(StatusBooking.STATUS_CONFIRMED)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }

            if (booking.getCheckInDate().isBefore(today) && booking.getCheckOutDate().isAfter(today)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }

            if (booking.getCheckInDate().isEqual(today) && booking.getCheckOutDate().isAfter(today)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }

            if (booking.getCheckInDate().isAfter(today) && booking.getCheckOutDate().isAfter(today)) {
                throw new BadRequestException(String.format("Status=%s is not available for this booking", booking.getStatus()));
            }
        }
    }
}
