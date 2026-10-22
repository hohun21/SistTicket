package org.doit.sistticket.category.service;

import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.dto.ConcertPageRequestDTO;
import org.doit.sistticket.category.dto.ConcertRequestDTO;
import org.doit.sistticket.category.dto.RoleRequestDTO;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.PerformanceCast;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface RoleService {

	Long registerRole(Long concertId, RoleRequestDTO dto);

	List<Role> getRolesByConcert(Long concertId);

	void deleteRole(Long roleId);

	void updateRole(Long roleId, RoleRequestDTO dto);


}
