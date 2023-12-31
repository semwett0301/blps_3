package com.example.code.services.WarehouseService;

import com.example.code.model.dto.web.response.ResponseAvailableBook;
import com.example.code.model.entities.Book;
import com.example.code.model.entities.Order;
import com.example.code.model.entities.Reservation;
import com.example.code.model.exceptions.BookIsNotAvailableException;
import com.example.code.model.mappers.BookMapper;
import com.example.code.model.modelUtils.ReservedBook;
import com.example.code.repositories.BookRepository;
import com.example.code.repositories.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceLitRes implements WarehouseService {

    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public WarehouseServiceLitRes(BookRepository bookRepository, ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<ResponseAvailableBook> getAllAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getAvailableAmount() > book.getReservations().size())
                .map(BookMapper.INSTANCE::toResponseAvailableBook)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void reserveBooks(List<ReservedBook> reservedBook, Order order) throws BookIsNotAvailableException {
        List<BookReservation> bookReservations = createBookReservationList(reservedBook);
        decreaseAvailableAmountOfBooks(bookReservations);
        createReservations(bookReservations, order);
        updateBooks(bookReservations);
    }

    @Override
    public void removeReservation(Integer orderId) {
        List<Reservation> reservations = reservationRepository.findAllByOrderNumber(orderId);
        reservationRepository.deleteAll(reservations);
    }

    private List<BookReservation> createBookReservationList(List<ReservedBook> requestReservedBooks) throws BookIsNotAvailableException {
        List<BookReservation> bookReservations = new ArrayList<>();
        List<Book> books = getRequestedBooks(requestReservedBooks);

        for (Book book : books) {
            final int requestedAmount = requestReservedBooks.stream()
                    .filter(e -> e.getId().equals(book.getId()))
                    .toList()
                    .get(0)
                    .getAmount();
            bookReservations.add(new BookReservation(book, requestedAmount));
        }

        return bookReservations;
    }

    private void updateBooks(List<BookReservation> bookReservations) {
        bookRepository.saveAll(bookReservations.stream()
                .map(BookReservation::getBook)
                .collect(Collectors.toList()));
    }

    private void createReservations(List<BookReservation> bookReservations, Order order) {
        List<Reservation> reservations = new ArrayList<>();
        bookReservations.forEach(bookReservation -> {
            for (int i = 0; i < bookReservation.getRequestedAmount(); i++) {
                reservations.add(new Reservation(bookReservation.getBook(), order));
            }
        });
        reservationRepository.saveAll(reservations);
    }

    private void decreaseAvailableAmountOfBooks(List<BookReservation> bookReservations) {
        bookReservations.forEach(bookReservation -> {
            Book book = bookReservation.getBook();
            book.setAvailableAmount(book.getAvailableAmount() - bookReservation.getRequestedAmount());
        });
    }

    private List<Book> getRequestedBooks(List<ReservedBook> reservedBookList) throws BookIsNotAvailableException {
        List<Book> books = getBooksByIds(reservedBookList.stream().map(ReservedBook::getId).collect(Collectors.toList()));
        if (reservedBookList.size() != books.size()) {
            throw new BookIsNotAvailableException();
        } else {
            return books;
        }
    }

    private List<Book> getBooksByIds(List<UUID> listIds) {
        return bookRepository.findAll().stream()
                .filter(book -> listIds.contains(book.getId()))
                .collect(Collectors.toList());
    }
}

@AllArgsConstructor
@Getter
@Setter
class BookReservation {
    private Book book;
    private int requestedAmount;
}
