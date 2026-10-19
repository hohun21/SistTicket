package org.doit.sistticket.category.service;

import java.util.List;

import org.doit.sistticket.category.dto.GenreDTO;
import org.doit.sistticket.category.entity.Genre;
import org.doit.sistticket.category.repository.GenreRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService{
	private final GenreRepository genreRepository;

    @Override
    public List<Genre> getGenreList() {
        return genreRepository.findAll();
    }

    @Override
    @Transactional
    public void saveGenre(GenreDTO dto) {
        Genre genre = Genre.builder()
                .genreName(dto.getGenreName())
                .build();
        genreRepository.save(genre);
    }

    @Override
    @Transactional
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
