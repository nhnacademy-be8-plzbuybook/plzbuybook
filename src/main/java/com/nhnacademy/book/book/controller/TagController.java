package com.nhnacademy.book.book.controller;

import com.nhnacademy.book.book.dto.request.TagRegisterDto;
import com.nhnacademy.book.book.dto.response.BookTagResponseDto;
import com.nhnacademy.book.book.dto.response.TagResponseDto;
import com.nhnacademy.book.book.service.Impl.BookService;
import com.nhnacademy.book.book.service.Impl.BookTagService;
import com.nhnacademy.book.book.service.Impl.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class TagController {

    private final TagService tagService;
    private final BookTagService bookTagService;

    @PostMapping("/tags")
    public ResponseEntity<Void> saveTag(@RequestBody TagRegisterDto tagRegisterDto) {
        tagService.save(tagRegisterDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




    @GetMapping("/tags")
    public ResponseEntity<Page<TagResponseDto>> getAllTags(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TagResponseDto> tags;

        if (keyword != null && !keyword.isEmpty()) {
            tags = tagService.searchTagsByKeyword(keyword, pageable);
        } else {
            tags = tagService.findAll(pageable);
        }

        return ResponseEntity.ok(tags);
    }

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<String> getTagNameByTagId(@PathVariable Long tagId) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.findTagNameByTagId(tagId));
    }


    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTagById(tagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/book-tags")
    public ResponseEntity<List<BookTagResponseDto>> getAllBookTags(@RequestParam Long tagId) {
        List<BookTagResponseDto> bookTags = bookTagService.getBookTagList(tagId);
        return ResponseEntity.ok(bookTags);
    }

    @PostMapping("/book-tags")
    public ResponseEntity<Void> saveBookTag(@RequestParam Long bookId, @RequestParam Long tagId) {
        bookTagService.save(bookId,tagId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/book-tags")
    public ResponseEntity<Void> deleteBookTag(@RequestParam Long bookId, @RequestParam Long tagId) {
        bookTagService.deleteBookTagList(bookId,tagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/book-tags/{bookId}")
    public ResponseEntity<List<BookTagResponseDto>> getBookTagsByBookId(@PathVariable Long bookId) {
        List<BookTagResponseDto> bookTags = bookTagService.getBookTagListByBookId(bookId);
        return ResponseEntity.ok(bookTags);
    }




}
