package com.capstone.HisTour.domain.region.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region")
@NoArgsConstructor
@Getter
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "road")
    private String road;

    @Builder
    public Region(String city, String district, String road) {
        this.city = city;
        this.district = district;
        this.road = road;
    }
}
