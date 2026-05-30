package com.service.Impl;

import com.entity.Cart;
import com.entity.Product;
import com.mapper.FindImageMapper;
import com.mapper.ProductMapper;
import com.service.IProductService;

import com.service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FindImageMapper findImageMapper;
    @Override
    public Product findByProduct_id(BigInteger id){
        Product product ;
        product = productMapper.findByProduct_id(id);
        if(product == null)
        {
            throw new ProductNotFoundException("该文物不存在");
        }
        if(product.getImg_url()==null) return product;
        String[] address = product.getImg_url().split(",");
        product.setImg_url(address[0]);
        product.setImg_url(findImageMapper.findImage(product.getImg_url()));
        return product;
    }


}
