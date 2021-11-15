package com.lucasdev.dscatalog.dto;

import com.lucasdev.dscatalog.entities.Category;

public class CategoryDTO {

	private Long id;
	private String name;

	public CategoryDTO() {

	}

	public CategoryDTO(Category entity) {
		this.id = entity.getId();
		this.name = entity.getName();
	}

	public CategoryDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
