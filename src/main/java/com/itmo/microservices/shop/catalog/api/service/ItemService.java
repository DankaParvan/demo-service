package com.itmo.microservices.shop.catalog.api.service;

import com.itmo.microservices.shop.catalog.api.model.BookingDescriptionDto;
import com.itmo.microservices.shop.catalog.api.model.BookingLogRecordDTO;
import com.itmo.microservices.shop.catalog.api.model.ItemDTO;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ItemService {
    @NotNull
    List<ItemDTO> listItems();
    @NotNull
    List<ItemDTO> listAvailableItems();
    @NotNull
    List<ItemDTO> listUnavailableItems();
    int getItemCount(@NotNull UUID id);
    void deleteItem(@NotNull UUID id);
    void createItem(@NotNull ItemDTO item);
    @NotNull
    void generateItems();
    void updateItem(@NotNull ItemDTO item);
    @NotNull
    List<BookingLogRecordDTO> listBookingLogRecords(@NotNull UUID bookingId);
    @NotNull
    ItemDTO describeItem(@NotNull UUID id);
    @NotNull
    BookingDescriptionDto book(@NotNull Map<UUID, Integer> items);
    @NotNull
    BookingDescriptionDto describeBooking(@NotNull UUID bookingId);
    void cancelBooking(@NotNull UUID bookingId);
    void processRefund(@NotNull UUID bookingId);
    void completeBooking(@NotNull UUID bookingId);
}
