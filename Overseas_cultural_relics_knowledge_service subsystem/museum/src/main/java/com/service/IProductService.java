package com.service;

import com.entity.Product;

import java.math.BigInteger;

public interface IProductService {
    Product findByProduct_id(BigInteger id);

}
