package com.lucasdev.dscatalog.tests;

import java.time.Instant;

import com.lucasdev.dscatalog.dto.ProductDTO;
import com.lucasdev.dscatalog.entities.Category;
import com.lucasdev.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		Product product = new Product(1L, "phone", "Good phone", 800.0,
		"https://img.com/img.png",Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(createCategory());
		return product;
	}
	
	public static ProductDTO createProducDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}

	public static Category createCategory() {
		return new Category(2L,"Eletronics");
	}

}
