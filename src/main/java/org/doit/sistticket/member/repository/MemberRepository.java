package org.doit.sistticket.member.repository;

import org.doit.sistticket.member.entity.Members;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Members,Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<Members> findByEmail(String email);
}
