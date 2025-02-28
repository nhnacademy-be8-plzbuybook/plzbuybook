package com.nhnacademy.book.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "selling_book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellingBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellingBookId;

    //판매책을 삭제할때는 책이 삭제 안되게
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingBookPrice;

    @Column(nullable = false)
    private Boolean sellingBookPackageable;

    @Column(name = "stock", nullable = false)
    private Integer sellingBookStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellingBookStatus sellingBookStatus;

    @Column(nullable = false)
    private Boolean used;

    @Column(nullable = false)
    private Long sellingBookViewCount;

    // SellingBookStatus enum
    public enum SellingBookStatus {
        SELLING,    // 판매중
        SELLEND,    // 판매 종료
        DELETEBOOK; // 삭제된 책

        // 기본값을 반환하는 안전한 valueOf 메서드 추가
        public static SellingBookStatus safeValueOf(String status) {
            try {
                return SellingBookStatus.valueOf(status);  // 유효한 Enum 값이면 그대로 반환
            } catch (IllegalArgumentException | NullPointerException e) {
                return SellingBookStatus.SELLING;  // 잘못된 값이나 null일 경우 기본값을 반환
            }
        }
    }

    public String getBookTitle() {
        return this.book.getBookTitle(); // Book 연관 객체에서 제목 가져오기
    }
}
