package com.nhnacademy.book.cartbook.service.impl;

import com.nhnacademy.book.book.entity.Book;
import com.nhnacademy.book.book.entity.BookImage;
import com.nhnacademy.book.book.entity.SellingBook;
import com.nhnacademy.book.book.exception.BookNotFoundException;
import com.nhnacademy.book.book.repository.BookImageRepository;
import com.nhnacademy.book.book.repository.SellingBookRepository;
import com.nhnacademy.book.cartbook.dto.request.CreateCartBookRequestDto;
import com.nhnacademy.book.cartbook.dto.response.ReadCartBookResponseDto;
import com.nhnacademy.book.cartbook.exception.BookStatusNotSellingBookException;
import com.nhnacademy.book.cartbook.repository.CartBookRedisRepository;
import com.nhnacademy.book.cartbook.service.CartBookGuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class CartBookGuestServiceImpl implements CartBookGuestService {
    private static final String PREFIX_GUEST = "Guest:";

    private final CartBookRedisRepository cartBookRedisRepository;
    private final SellingBookRepository sellingBookRepository;
    private final BookImageRepository bookImageRepository;

    public CartBookGuestServiceImpl(CartBookRedisRepository cartBookRedisRepository,
                                    SellingBookRepository sellingBookRepository,
                                    BookImageRepository bookImageRepository) {
        this.cartBookRedisRepository = cartBookRedisRepository;
        this.sellingBookRepository = sellingBookRepository;
        this.bookImageRepository = bookImageRepository;
    }


    @Override
    public Long addToGuestCart(CreateCartBookRequestDto createCartBookRequestDto, String sessionId) {
        Long cartId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        SellingBook sellingBook = sellingBookRepository.findById(createCartBookRequestDto.sellingBookId())
                .orElseThrow(() -> new BookNotFoundException("비회원 장바구니에서 찾는 도서가 존재하지 않습니다."));
        if (!sellingBook.getSellingBookStatus().equals(SellingBook.SellingBookStatus.SELLING)) {
            throw new BookStatusNotSellingBookException("판매중인 도서가 아닙니다.");
        }

        List<ReadCartBookResponseDto> existingCartItems = cartBookRedisRepository.readAllHashName(PREFIX_GUEST + sessionId);
        if (cartBookRedisRepository.isHit(PREFIX_GUEST + sessionId)) {
            for (ReadCartBookResponseDto existingCartItem : existingCartItems) {
                if (existingCartItem.sellingBookId().equals(sellingBook.getSellingBookId())) {
                    int quantity = existingCartItem.quantity() + createCartBookRequestDto.quantity();
                    return cartBookRedisRepository.update(PREFIX_GUEST + sessionId, existingCartItem.cartId(), quantity);
                }
            }
        }

        Long cartBookId = (long) (existingCartItems.size() + 1); // 자동으로 증가

        ReadCartBookResponseDto readCartBookResponseDto = ReadCartBookResponseDto.builder()
                .cartId(cartId)
                .cartBookId(cartBookId)
                .sellingBookId(sellingBook.getSellingBookId())
                .bookTitle(sellingBook.getBookTitle())
                .sellingBookPrice(sellingBook.getSellingBookPrice())
                .imageUrl(getImageUrl(sellingBook, bookImageRepository))
                .quantity(createCartBookRequestDto.quantity())
                .sellingBookStock(sellingBook.getSellingBookStock())
                .build();
        return cartBookRedisRepository.create(PREFIX_GUEST + sessionId, cartId, readCartBookResponseDto);

    }


    @Override
    public Long updateGuestCartItem(Long cartId, int quantity, String sessionId) {
        return cartBookRedisRepository.update(PREFIX_GUEST + sessionId, cartId, quantity);
    }

    @Override
    public Long removeItemFromGuestCart(Long cartId, String sessionId) {
        return cartBookRedisRepository.delete(PREFIX_GUEST + sessionId, cartId);
    }

    @Override
    public void clearGuestCart(String sessionId) {
        cartBookRedisRepository.deleteAll(PREFIX_GUEST + sessionId);
    }

    @Override
    public List<ReadCartBookResponseDto> getGuestCart(String sessionId) {
        return cartBookRedisRepository.readAllHashName(PREFIX_GUEST + sessionId);
    }

    private String getImageUrl(SellingBook sellingBook, BookImageRepository bookImageRepository) {
        Book book = sellingBook.getBook(); // 판매책으로 책정보를 가져옴
        BookImage bookImage = bookImageRepository.findByBook(book).orElse(null); // 책으로 책이미지를 가져옴
        String url = bookImage != null ? bookImage.getImageUrl() : null; // 책이미지에서 이미지 url 을 가져옴

        if (url != null && !url.isEmpty()) {
            log.info("책에 대한 이미지 url이 존재하지 않습니다.");
        }
        return url;
    }


}
