package org.doit.sistticket.category.entity;

import org.doit.sistticket.actor.entity.Actor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Setter
@Table(name="performance_casts")
public class PerformanceCast {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "performance_cast_id", nullable=false)
    private long performanceCastId;
    
	//performances - performances_casts
    @ManyToOne
    @JoinColumn(name = "performance_id", nullable=false)
    private Performances performance;
    
    //performance_casts - roles (배역이 삭제될 때의 정책에 따라 설정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL) // 배역이 지워지면 role_id를 NULL
    private Role role;
    
    // performances_casts - actors 배우가 삭제되면 actor_id를 NULL로 허용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL) // 배우가 지워지면 actor_id를 NULL
    private Actor actor;
	
}
