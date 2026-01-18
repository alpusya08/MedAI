package com.medai.model.enums;

public enum AppointmentStatus {
    PENDING,    // Ожидает подтверждения врача
    CONFIRMED,  // Подтверждена врачом
    CANCELLED,  // Отменена
    COMPLETED,  // Завершена
    NO_SHOW     // Пациент не явился
}
