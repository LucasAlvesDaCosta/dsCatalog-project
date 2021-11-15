package com.lucasdev.dscatalog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasdev.dscatalog.dto.CategoryDTO;
import com.lucasdev.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResouce {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> findAll() {
		
		List<CategoryDTO> list = categoryService.findAll();
		return ResponseEntity.ok().body(list);
	}
}
