package org.doit.sistticket.category.service;

import org.doit.sistticket.category.dto.PerformanceRequestDTO;

public interface PerformanceService {

	void registerPerformance(Long concertId, PerformanceRequestDTO dto);

	void deletePerformance(Long performanceId);

	void updatePerformance(Long performanceId, PerformanceRequestDTO dto);
	

}
