package org.doit.sistticket.category.service;

import java.util.List;

import org.doit.sistticket.category.dto.GenreDTO;
import org.doit.sistticket.category.entity.Genre;

public interface GenreService {
	List<Genre> getGenreList();       // 전체 장르 목록 조회 (콘서트 폼 셀렉트박스용)
    void saveGenre(GenreDTO dto);     // 장르 등록
    void deleteGenre(Long id);        // 장르 삭제
}
