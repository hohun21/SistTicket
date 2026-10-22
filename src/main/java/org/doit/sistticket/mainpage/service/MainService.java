package org.doit.sistticket.mainpage.service;

import java.util.List;

import org.doit.sistticket.mainpage.dto.MainConcertDTO;

public interface MainService {
    List<MainConcertDTO> getLatestConcerts();
}
