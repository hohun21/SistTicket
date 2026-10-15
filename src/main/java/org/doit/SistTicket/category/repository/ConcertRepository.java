package org.doit.sistticket.category.repository;

import org.doit.sistticket.category.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert,Long>{

}
