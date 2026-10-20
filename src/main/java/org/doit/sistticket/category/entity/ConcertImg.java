package org.doit.sistticket.category.entity;

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
@Setter
@ToString(exclude = "concert")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "concert_imgs")
public class ConcertImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_img_id", nullable = false)
    private long concertImgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @Column(name = "is_thumbnail", nullable = false)
    private boolean isThumbnail; 

    @Column(name = "img_order")
    private int imgOrder;
    
    private String uuid;      // 파일 중복 방지 고유 번호
    private String imgName;   // 원본 파일 이름
    private String path;      // 업로드 저장 경로 (날짜별 폴더 등)
}