package com.nhnacademy.book.book.dto.request;

import com.nhnacademy.book.book.entity.SellingBook.SellingBookStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data

@Getter
@Setter
public class SellingBookRegisterDto {
    private Long bookId; // 책 ID
    private BigDecimal sellingBookPrice; // 판매가
    private Boolean sellingBookPackageable; // 선물 포장 가능 여부
    private Integer sellingBookStock; // 재고
    private SellingBookStatus sellingBookStatus;
    private Long sellingBookViewCount;//조회수
    private Boolean used; // 중고 여부


}
