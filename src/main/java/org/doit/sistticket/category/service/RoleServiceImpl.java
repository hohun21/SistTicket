package org.doit.sistticket.category.service;

import java.util.List;

import org.doit.sistticket.category.dto.RoleRequestDTO;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.Role;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.PerformanceCastRepository;
import org.doit.sistticket.category.repository.RoleRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
	private final RoleRepository roleRepository;
    private final ConcertRepository concertRepository;
    private final PerformanceCastRepository performanceCastRepository;


    // 배역 등록
    @Override
    public Long registerRole(Long concertId, RoleRequestDTO dto) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘서트입니다. ID: " + concertId));

        Role role = Role.builder()
                .concert(concert)
                .roleName(dto.getRoleName())
                .description(dto.getDescription())
                .build();

        return roleRepository.save(role).getRoleId();
    }

    // 특정 콘서트의 배역 목록 조회
    @Override
    @Transactional
    public List<Role> getRolesByConcert(Long concertId) {
        return roleRepository.findByConcert_ConcertId(concertId);
    }

    
    //배역 수정 (Role Update)
    @Override
    @Transactional
    public void updateRole(Long roleId, RoleRequestDTO dto) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배역입니다. ID: " + roleId));
        
        // 배역 이름과 설명 수정
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
    }

    // 2. 배역 안전 삭제 (Role Delete)
    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        // ① 먼저 해당 배역에 묶인 performance_casts 데이터를 지워줍니다.
        performanceCastRepository.deleteByRole_RoleId(roleId);
        
        // ② 배역(Role) 엔티티를 지웁니다.
        roleRepository.deleteById(roleId);
    }
}
