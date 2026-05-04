package com.mapper;

import com.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
@Repository
@Mapper
public interface ProductMapper {
    Product findByProduct_id(BigInteger id);
}
