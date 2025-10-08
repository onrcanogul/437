package com.example.demo.starter.infrastructure.configuration.mapper;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.product.ProductDto;
import com.example.demo.starter.domain.entity.base.BaseEntity;
import com.example.demo.starter.domain.entity.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public Mapper<BaseEntity, BaseDto> baseMapper() {
        return new Mapper<>(BaseEntity.class, BaseDto.class);
    }
    @Bean
    public Mapper<Product, ProductDto> productMapper() {
        return new Mapper<>(Product.class, ProductDto.class);
    }
}