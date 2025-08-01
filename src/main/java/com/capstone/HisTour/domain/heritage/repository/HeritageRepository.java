package com.capstone.HisTour.domain.heritage.repository;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HeritageRepository extends JpaRepository<Heritage, Long> {

//    @Query(value = """
//    SELECT *,
//           ST_Distance(
//                geom::geography,\s
//                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
//           ) AS distance
//    FROM heritage
//    WHERE ST_DWithin(
//        geom::geography,
//        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
//        :radius
//    )
//    ORDER BY distance ASC
//    """, nativeQuery = true)
//    List<Heritage> findNearbyHeritages(@Param("latitude") double latitude,
//                                       @Param("longitude") double longitude,
//                                       @Param("radius") double radius);

    @Query(value = """
    SELECT *
    FROM heritage
    WHERE ST_DWithin(
        geom,
        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
        :radius/111320
    )
    """, nativeQuery = true)
    List<Heritage> findNearbyHeritages(@Param("latitude") double latitude,
                                       @Param("longitude") double longitude,
                                       @Param("radius") double radius);

    // 유사 검색을 위한 메서드
    List<Heritage> findByNameContainingIgnoreCase(String name);
}
