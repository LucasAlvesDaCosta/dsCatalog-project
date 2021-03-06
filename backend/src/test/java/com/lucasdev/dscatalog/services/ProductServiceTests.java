package com.lucasdev.dscatalog.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.lucasdev.dscatalog.dto.ProductDTO;
import com.lucasdev.dscatalog.entities.Category;
import com.lucasdev.dscatalog.entities.Product;
import com.lucasdev.dscatalog.repositories.CategoryRepository;
import com.lucasdev.dscatalog.repositories.ProductRepository;
import com.lucasdev.dscatalog.services.exceptions.DataBaseException;
import com.lucasdev.dscatalog.services.exceptions.ResourceNotFoundException;
import com.lucasdev.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO productDTO;
	private Category category;

	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		productDTO = Factory.createProducDTO();
		category = Factory.createCategory();
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.find(any(),any(),any())).thenReturn(page);

		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

	

		Mockito.doNothing().when(repository).deleteById(existingId);
		
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
	}
	
	@Test
	public void updateShoudUpdateValueWhenIdExist() {
        ProductDTO dto = service.update(existingId, productDTO);		
		Assertions.assertNotNull(dto);
		}
	
	@Test
	public void updateShoudThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});
		
		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
	}
	
	@Test
	public void findByIdShoudThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
		
	}
	
	@Test
	public void findByIdShoudReturnProductDTOWhenExistId() {
		productDTO = service.findById(existingId);
		
		Assertions.assertNotNull(productDTO);
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
		
	}
	
	@Test
	public void findAllPagedShoudReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(0L,"", pageable);
		
		Assertions.assertNotNull(result);
		///Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
		
	}
	
	@Test
	public void deleteShoudThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShoudThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShoudDoNothingWhenIdExist() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	

}
