package com.nhnacademy.book.book.repository;

import com.nhnacademy.book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ISBN 13으로 책 조회
    Book findByBookIsbn13(String bookIsbn13);

    // 중복 ISBN 13 체크
    boolean existsByBookIsbn13(String bookIsbn13);

    // 제목으로 책 조회
    List<Book> findByBookTitle(String bookTitle);

    Book findByBookId(Long bookId);


    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN BookAuthor ba ON b.bookId = ba.book.bookId " +
            "LEFT JOIN Author a ON ba.author.authorId = a.authorId " +
            "LEFT JOIN BookCategory bc ON b.bookId = bc.book.bookId " +
            "LEFT JOIN Category c ON bc.category.categoryId = c.categoryId " +
            "WHERE " +
            "(:searchKeyword IS NOT NULL AND " +
            "   (a.authorName LIKE %:searchKeyword% AND b.bookTitle NOT LIKE %:searchKeyword% AND c.categoryName NOT LIKE %:searchKeyword%) " + // 작가 검색 시, 책 제목과 카테고리에는 검색되지 않음
            "OR :searchKeyword IS NOT NULL AND " +
            "   (b.bookTitle LIKE %:searchKeyword% AND a.authorName NOT LIKE %:searchKeyword% AND c.categoryName NOT LIKE %:searchKeyword%) " + // 책 제목 검색 시, 작가와 카테고리에는 검색되지 않음
            "OR :searchKeyword IS NOT NULL AND " +
            "   (c.categoryName LIKE %:searchKeyword% AND a.authorName NOT LIKE %:searchKeyword% AND b.bookTitle NOT LIKE %:searchKeyword%))")
    Page<Book> findBooksBySearchKeyword(@Param("searchKeyword") String searchKeyword, Pageable pageable);


    @Query("SELECT COUNT(b) FROM Book b " +
            "LEFT JOIN BookAuthor ba ON b.bookId = ba.book.bookId " +
            "LEFT JOIN Author a ON ba.author.authorId = a.authorId " +
            "LEFT JOIN BookCategory bc ON b.bookId = bc.book.bookId " +
            "LEFT JOIN Category c ON bc.category.categoryId = c.categoryId " +
            "WHERE " +
            "(:searchKeyword IS NOT NULL AND (" +
            "(a.authorName LIKE %:searchKeyword% AND b.bookTitle NOT LIKE %:searchKeyword% AND c.categoryName NOT LIKE %:searchKeyword%) " + // 작가 검색 시
            "OR (b.bookTitle LIKE %:searchKeyword% AND a.authorName NOT LIKE %:searchKeyword% AND c.categoryName NOT LIKE %:searchKeyword%) " + // 책 제목 검색 시
            "OR (c.categoryName LIKE %:searchKeyword% AND a.authorName NOT LIKE %:searchKeyword% AND b.bookTitle NOT LIKE %:searchKeyword%)) " + // 카테고리 검색 시
            "OR :searchKeyword IS NULL)")
    long countBooksBySearchKeyword(@Param("searchKeyword") String searchKeyword);



    @Query("SELECT b FROM Book b JOIN b.bookAuthors ba JOIN ba.author a WHERE a.authorName LIKE %:keyword%")
    List<Book> findByBookAuthorContaining(@Param("keyword") String keyword);

    // 카테고리 이름에 키워드 포함
    @Query("SELECT b FROM Book b JOIN b.bookCategories bc JOIN bc.category c WHERE c.categoryName LIKE %:keyword%")
    List<Book> findByCategoryContaining(@Param("keyword") String keyword);


    @Query("SELECT b FROM Book b WHERE b.bookId NOT IN (SELECT sb.book.bookId FROM SellingBook sb)")
    Page<Book> findBooksNotInSellingBooks(Pageable pageable);


    // 제목에 특정 문자열이 포함된 책 조회
    Page<Book> findByBookTitleContaining(String bookTitle,Pageable pageable);

    // index에 특정 문자열 조회
    List<Book> findByBookIndexContaining(String bookIndex);

    // 특정 출판사의 책 조회
    List<Book> findByPublisher_PublisherId(Long publisherId);

    // 특정 카테고리에 속한 책 조회
    List<Book> findByBookCategories_Category_CategoryId(Long categoryId);

    boolean existsByBookId(Long bookId);

}
