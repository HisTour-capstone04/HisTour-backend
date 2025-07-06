package com.capstone.HisTour.domain.heritage.domain;

import com.capstone.HisTour.domain.region.domain.Region;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "heritage", indexes = @Index(name = "idx_heritage_geom", columnList = "geom"))
@NoArgsConstructor
@Getter
public class Heritage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "detail_address", nullable = false, columnDefinition = "TEXT")
    private String detailAddress;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "geom", nullable = false)
    private Point geom;

    @Column(name = "ccbakdcd")
    private String categoryCode;

    @Column(name = "ccbaasno")
    private String manageNum;

    @Column(name = "ccbactcd")
    private String locationCode;

    @Column(name = "era")
    private String era;

    @Column(name = "type")
    private String type;

    @Column(name = "side")
    private String side;

    @Builder
    public Heritage(String name, String category, String detailAddress, String description, Region region, Point geom, String categoryCode, String manageNum, String locationCode,  String era, String type,  String side) {
        this.name = name;
        this.category = category;
        this.detailAddress = detailAddress;
        this.description = description;
        this.region = region;
        this.geom = geom;
        this.categoryCode = categoryCode;
        this.manageNum = manageNum;
        this.locationCode = locationCode;
        this.era = era;
        this.type = type;
        this.side = side;
    }

    @Override
    public String toString() {
        return "id: " + this.getId() + ", name: " + this.getName()
                + ", category: " + this.getCategory()
                + ", era: " + this.getEra() + ", side: " + this.getSide();
    }
}
