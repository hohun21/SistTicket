package org.doit.sistticket.category.service;

import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.repository.ActorRepository;
import org.doit.sistticket.category.dto.PerformanceRequestDTO;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.PerformanceCast;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.entity.Role;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.PerformanceCastRepository;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.doit.sistticket.category.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformancesRepository performancesRepository;
    private final PerformanceCastRepository performanceCastRepository;
    private final ConcertRepository concertRepository;
    private final RoleRepository roleRepository;
    private final ActorRepository actorRepository;

    @Override
    public void registerPerformance(Long concertId, PerformanceRequestDTO dto) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘서트입니다."));

        
        //  해당 콘서트의 기존 최대 회차 번호 조회 
        Integer maxSeq = performancesRepository.findMaxSeqByConcertId(concertId);
        int nextSeq = (maxSeq == null) ? 1 : maxSeq + 1;
        
        // 1. 회차(Performances) 정보 저장
        Performances performance = Performances.builder()
                .concert(concert)
                .performanceSeq(nextSeq)
                .performanceDate(dto.getPerformanceDate())
                .bookingStart(dto.getBookingStart())
                .bookingEnd(dto.getBookingEnd())
                .status(dto.getStatus())
                .build();
        
        Performances savedPerformance = performancesRepository.save(performance);

        // 2. 함께 넘어온 캐스팅 정보(배역-배우 매핑)가 있다면 저장
        if (dto.getCasts() != null) {
            for (PerformanceRequestDTO.CastRequestDTO castDto : dto.getCasts()) {
                Role role = roleRepository.findById(castDto.getRoleId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배역입니다."));
                
                Actor actor = actorRepository.findById(castDto.getActorId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배우입니다."));

                PerformanceCast performanceCast = PerformanceCast.builder()
                        .performance(savedPerformance)
                        .role(role)
                        .actor(actor)
                        .build();

                performanceCastRepository.save(performanceCast);
            }
        }
    }
    
 // 1. 회차 수정 (일시, 예매기간, 상태, 배역별 배우 캐스팅 포함)
    @Override
    @Transactional
    public void updatePerformance(Long performanceId, PerformanceRequestDTO dto) {
        Performances performance = performancesRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다. ID: " + performanceId));

        // 회차 기본 정보 및 상태 변경
        performance.setPerformanceDate(dto.getPerformanceDate());
        performance.setBookingStart(dto.getBookingStart());
        performance.setBookingEnd(dto.getBookingEnd());
        performance.setStatus(dto.getStatus()); // UPCOMING, ONGOING, FINISHED 상태 변경

        // 캐스팅 재설정: 기존 캐스팅을 싹 지우고 새로 입력받은 캐스팅 정보로 대체
        performanceCastRepository.deleteByPerformance_PerformanceId(performanceId);

        if (dto.getCasts() != null) {
            for (PerformanceRequestDTO.CastRequestDTO castDto : dto.getCasts()) {
                Role role = roleRepository.findById(castDto.getRoleId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배역입니다."));
                Actor actor = actorRepository.findById(castDto.getActorId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배우입니다."));

                PerformanceCast performanceCast = PerformanceCast.builder()
                        .performance(performance)
                        .role(role)
                        .actor(actor)
                        .build();

                performanceCastRepository.save(performanceCast);
            }
        }
    }

    // 2. 회차 삭제 (Performance Delete)
    @Override
    @Transactional
    public void deletePerformance(Long performanceId) {
        // ① 회차에 속한 performance_casts 먼저 삭제
        performanceCastRepository.deleteByPerformance_PerformanceId(performanceId);
        
        // ② 회차 엔티티 삭제
        performancesRepository.deleteById(performanceId);
    }
}