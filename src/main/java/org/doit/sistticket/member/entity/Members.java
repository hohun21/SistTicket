package org.doit.sistticket.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name ="members")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Members extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="members_id")
    private Long memberId;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false,length = 100)
    private String phone;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="members_roles",
    joinColumns = @JoinColumn(name="members_id")
    )

    @Column(name="role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    private int status;


}
/*member_id(PK)	N/A	N/A
email	N/A	N/A
password	N/A	N/A
name	N/A	N/A
phone	N/A	N/A
role	N/A	N/A
status	N/A	N/A
created_at	N/A	N/A
updated_at	N/A	N/A*/
