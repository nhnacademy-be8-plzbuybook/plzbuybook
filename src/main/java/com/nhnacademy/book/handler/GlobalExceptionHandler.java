package com.nhnacademy.book.handler;

import com.nhnacademy.book.member.domain.dto.ErrorResponseDto;
import com.nhnacademy.book.member.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //중복 이메일
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateEmailException(DuplicateEmailException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    //회원 등급을 찾을 수 없는 예외 처리
    @ExceptionHandler(MemberGradeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberGradeNotFoundException(MemberGradeNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    //회원 상태를 찾을 수 없는 예외 처리
    @ExceptionHandler(MemberStatusNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberStatusNotFoundException(MemberStatusNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    //기본 등급을 찾을 수 없는 예외 처리
    @ExceptionHandler(DefaultMemberGradeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleDefaultMemberGradeNotFoundException(DefaultMemberGradeNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    //기본 상태를 찾을 수 없는 예외 처리
    @ExceptionHandler(DefaultStatusGradeNotfoundException.class)
    public ResponseEntity<ErrorResponseDto> handleDefaultStatusGradeNotFoundException(DefaultStatusGradeNotfoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    //이메일로 특정 회원을 조회 할 때 찾을 수 없는 예외 처리
    @ExceptionHandler(MemberEmailNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberEmailNotFoundException(MemberEmailNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    //id로 특정 회원을 조회 할 떄 찾을 수 없는 예외 처리
    @ExceptionHandler(MemberIdNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberIdNotFoundException(MemberIdNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    //상태를 추가할 때 중복
    @ExceptionHandler(DuplicateMemberStateException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateMemberStateException(DuplicateMemberStateException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    //등급을 추가할 때 중복
    @ExceptionHandler(DuplicateMemberGradeException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateMemberGradeException(DuplicateMemberGradeException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    //회원을 수정할 떄 기존 값과 같을 경우
    @ExceptionHandler(DuplicateMemberModificationException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateMemberModificationException(DuplicateMemberModificationException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    //전체 회원을 조회할 때 등록된 회원이 없는 경우
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberNotFoundException(MemberNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }
}
