package com.nhnacademy.book.book.elastic.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter


@RequiredArgsConstructor
@AllArgsConstructor
@Document(indexName = "book_image_4")

public class BookImageDocument {
    @org.springframework.data.annotation.Id
    private Long bookImageId;

    private Long bookId;

    private String imageUrl;
}

