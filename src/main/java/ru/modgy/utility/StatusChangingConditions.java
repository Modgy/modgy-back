package ru.modgy.utility;

public class StatusChangingConditions {
    public static final String CONDITION_TO_PUT_CHECKED_IN = "Для смены статуса на Заселен требуется изменение даты " +
            "начала бронирования на <= текущая дата + должен быть выбран доступный номер. ";
    public static final String CONDITION_TO_PUT_INITIAL = "Для смены статуса на Первичное, если Предоплата внесена = " +
            "Да, смена статуса невозможна. Нужно установить в поле Предоплата внесена значение = Нет. ";
    public static final String CONDITION_TO_PUT_CHECKED_OUT = "Для смены статуса на Выселен требуется изменить " +
            "дату окончания бронирования на текущую дату.  ";
    public static final String CONDITION_TO_SWITCH_CANCELLED = "Должен быть выбран доступный номер. ";
}
