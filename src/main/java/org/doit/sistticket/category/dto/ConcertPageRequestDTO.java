package org.doit.sistticket.category.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class ConcertPageRequestDTO {
    private int page = 0;      // 
    private int size = 9;      //
    private Long genreId;      //
    private String category;   // ('all', 'concert', 'musical')
    
    //검색용 필드 추가
    private String searchKeyword;      // 검색 키워드 (공연명, 장소명 등)
    private String type;               // 검색 타입 ("n"=제목, "v"=장소, "nv"=제목+장소)
    
    // 페이징 객체 생성 편의 메서드
    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page <= 0 ? 0 : page, size, sort);
    }
}