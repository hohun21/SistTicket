package org.doit.sistticket.category;

import java.util.ArrayList;
import java.util.List;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.ConcertImg;
import org.doit.sistticket.category.repository.ConcertImgRepository;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConcertImgRepositoryTests {

    @Autowired
    private ConcertImgRepository concertImgRepository;

    @Autowired
    private ConcertRepository concertRepository;
/*
    @Test
    void insertConcertImgList() {
        List<Concert> concerts = concertRepository.findAll();

        if (concerts.isEmpty()) {
            throw new IllegalStateException("Concert 데이터가 먼저 입력되어 있어야 합니다!");
        }

        List<ConcertImg> imgList = new ArrayList<>();

        for (Concert concert : concerts) {
            long concertId = concert.getConcertId();

            for (int i = 1; i <= 1; i++) {
                String imgUrl = "concert_" + concertId + "_" + i + ".jpg";

                imgList.add(
                    ConcertImg.builder()
                        .concert(concert)
                        .imgUrl(imgUrl)
                        .imgOrder(i)
                        .build()
                );
            }
        }

        concertImgRepository.saveAll(imgList);
    }
*/
}