package com.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ArtifactImageMapper {

    String findImageUrlByObjectId(@Param("objectId") String objectId);

    String findImageUrlByAccessionNumber(@Param("accessionNumber") String accessionNumber);

    List<Map<String, String>> findImageRowsByObjectIds(@Param("objectIds") List<String> objectIds);

    List<Map<String, String>> findImageRowsByAccessionNumbers(@Param("accessionNumbers") List<String> accessionNumbers);

    List<Map<String, String>> findMetaRowsByObjectIds(@Param("objectIds") List<String> objectIds);
}
