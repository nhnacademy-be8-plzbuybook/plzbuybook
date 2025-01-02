package com.nhnacademy.book.book.elastic.repository;

import com.nhnacademy.book.book.elastic.document.CategoryDocument;
import com.nhnacademy.book.book.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategorySearchRepository extends ElasticsearchRepository<CategoryDocument, Long> {
    Optional<CategoryDocument> findByCategoryName(String categoryName);

    Optional<CategoryDocument> findByCategoryNameAndParentCategory(String categoryName, Category parentCategory);

    List<CategoryDocument> findByParentCategory(Category parentCategory);

//    @Query("{\"match\": {\"category_name\": {\"query\": \"?0\", \"operator\": \"and\"}}}")
//    List<CategoryDocument> findByCategoryNameContaining(String keyword);



    @Query("{\"match\": {\"category_name\": {\"query\": \"?0\", \"operator\": \"and\"}}}")
    Page<CategoryDocument> findByCategoryNameContaining(String keyword, Pageable pageable);


}
