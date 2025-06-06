package org.example.systempaleteria.productos.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import org.example.systempaleteria.productos.entities.ProductModel;
import org.example.systempaleteria.productos.entities.dto.ProductDetailsDTO;
import org.example.systempaleteria.productos.entities.dto.ProductDto;
import org.example.systempaleteria.productos.mappers.ProductMapper;
import org.example.systempaleteria.productos.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import   jakarta.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.View;

import java.util.NoSuchElementException;
import java.util.Optional;

import java.util.List;
import java.util.Set;

@Service
@Validated(ProductService.class)
public class ProductService {


    private final ProductRepository productRepository;

    private final ProductMapper productMapper;
    private final Validator validator;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, Validator validator, View error) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.validator = validator;

    }

    public List<ProductDto> getAllProducts() {

        return  this.productRepository.findAll().stream()
                .map(this.productMapper::productToProductDto).toList();

    }

    public List<ProductDetailsDTO> getAllDetailsProducts() {

        return  this.productRepository.findAll().stream()
                .map(this.productMapper::productDetailsToDto).toList();

    }

    public  ProductDto getProductById(Long id) {

        Optional<ProductModel> productDto = this.productRepository.findById(id);

        if (productDto.isPresent()) {
            return this.productMapper.productToProductDto(productDto.get());
        }else
            throw new NoSuchElementException("Product not found");


    }

    @Transactional
    public ProductModel saveProduct(  ProductDto productDto) {



        Set<ConstraintViolation<ProductDto>> violations = this.validator.validate(productDto);

        if (!violations.isEmpty()) {

            StringBuilder message = new StringBuilder("Errores de validacion ");

            for (ConstraintViolation<ProductDto> violation : violations) {
                message.append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
            }

            System.out.println(message);

            throw  new IllegalCallerException(message.toString());

        }
        return   this.productRepository.save(this.productMapper.dtoToProductModel(productDto));






    }




}
