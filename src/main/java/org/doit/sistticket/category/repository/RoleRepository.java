package org.doit.sistticket.category.repository;

import java.util.List;

import org.doit.sistticket.category.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long>{
	// 특정 콘서트에 등록된 배역(역할) 목록 조회
    List<Role> findByConcert_ConcertId(Long concertId);

    
    void deleteByConcert_ConcertId(Long concertId);
}
