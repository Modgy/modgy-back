--liquibase formatted sql
--changeset dka:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

INSERT INTO bookings (type_bookings, check_in_date_bookings, check_out_date_bookings,
                      check_in_time_bookings, check_out_time_bookings, status_bookings, reason_of_stop_bookings,
                      reason_of_cancel_bookings, price_bookings, amount_bookings, prepayment_amount_bookings,
                      made_prepayment_bookings, comment_bookings, file_bookings, room_id_bookings)
VALUES ('TYPE_BOOKING', '2024-09-02', '2024-09-09', '11:43:31', '11:43:34', 'STATUS_CONFIRMED', null, null, 0, 0, 0,
        false, null, null, 1);