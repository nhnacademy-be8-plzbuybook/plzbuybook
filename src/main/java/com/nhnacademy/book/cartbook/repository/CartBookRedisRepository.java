package com.nhnacademy.book.cartbook.repository;

import com.nhnacademy.book.cartbook.dto.response.ReadGuestCartBookResponseDto;

import java.util.List;

public interface CartBookRedisRepository {
    Long create (String hashName, Long id, ReadGuestCartBookResponseDto readGuestCartBookResponseDto);
    Long update (String hashName, Long id, int quantity);
    Long delete (String hashName, Long id);
    void deleteAll (String hashName);
    List<ReadGuestCartBookResponseDto> readAllHashName(String hashName);
    boolean isHit(String hashName);
    boolean isMiss(String hashName);
    void loadData(List<ReadGuestCartBookResponseDto> bookCartGuestResponses, String hashName);
}
