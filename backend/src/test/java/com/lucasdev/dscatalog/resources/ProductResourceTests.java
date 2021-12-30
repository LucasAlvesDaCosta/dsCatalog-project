package com.lucasdev.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasdev.dscatalog.dto.ProductDTO;
import com.lucasdev.dscatalog.services.ProductService;
import com.lucasdev.dscatalog.services.exceptions.DataBaseException;
import com.lucasdev.dscatalog.services.exceptions.ResourceNotFoundException;
import com.lucasdev.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService service;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	
	@BeforeEach
	void setup() throws Exception {
		
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		productDTO = Factory.createProducDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		//when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.insert(any())).thenReturn(productDTO);

		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		// to methods void...
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DataBaseException.class).when(service).delete(dependentId);

	}
	
	@Test
	public void insertShouldReturnCreated() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions resultActions = 
				mockMvc.perform(post("/products")
						.content(jsonBody)
					    .contentType(MediaType.APPLICATION_JSON)
				        .accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.id").exists());
		resultActions.andExpect(jsonPath("$.name").exists());
		resultActions.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception{
		
		ResultActions resultActions = mockMvc
				       .perform(delete("/products/{id}",existingId));
		resultActions.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoNotExist() throws Exception{
		
		ResultActions resultActions = mockMvc
				       .perform(delete("/products/{id}",nonExistingId));
		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() throws Exception{
		
		ResultActions resultActions = mockMvc
				       .perform(delete("/products/{id}",dependentId));
						resultActions.andExpect(status().isBadRequest());

	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions resultActions = mockMvc
				       .perform(put("/products/{id}",existingId)
				       .content(jsonBody)
				       .contentType(MediaType.APPLICATION_JSON)
				       .accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").exists());
		resultActions.andExpect(jsonPath("$.name").exists());
		resultActions.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		ResultActions resultActions = mockMvc
			       .perform(put("/products/{id}",nonExistingId)
			       .content(jsonBody)
			       .contentType(MediaType.APPLICATION_JSON)
			       .accept(MediaType.APPLICATION_JSON));
		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShoudReturnPage() throws Exception{
		ResultActions resultActions = 
				mockMvc.perform(get("/products")
				       .accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions resultActions = 
				mockMvc.perform(get("/products/{id}",existingId)
				       .accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").exists());
		resultActions.andExpect(jsonPath("$.name").exists());
		resultActions.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions resultActions = 
				mockMvc.perform(get("/products/{id}",nonExistingId)
				       .accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isNotFound());
	}
}
