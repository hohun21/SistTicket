package org.doit.sistticket.member.repository;

import org.doit.sistticket.member.entity.Members;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Members,Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<Members> findByEmail(String email);

    @Query("""
        SELECT m.email
        FROM members m WHERE m.name= :name AND m.phone = :phone 
        """)
    String findIdByNameAndPhone(String name, String phone);

    Optional<Members> findByEmailAndNameAndPhone(
            String email, String name, String phone
    );

    boolean existsByEmail(String email);

    @Query("""
            SELECT count(m)
            FROM members m
            WHERE m.status = 1 
        """)
    Long countByStatus();
}
