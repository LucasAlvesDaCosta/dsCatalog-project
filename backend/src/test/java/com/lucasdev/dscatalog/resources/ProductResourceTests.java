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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import com.lucasdev.dscatalog.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@MockBean
	private ProductService service;
	
	private String username;
	private String password;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;

	@BeforeEach
	void setup() throws Exception {
		username = "maria@gmail.com";
		password = "123456";
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		productDTO = Factory.createProducDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any(), any(),any())).thenReturn(page);
		
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
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions resultActions = 
				mockMvc.perform(post("/products")
					    .header("Authorization", "Bearer " + accessToken)
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
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions resultActions = mockMvc
				       .perform(delete("/products/{id}",existingId)
					   .header("Authorization", "Bearer " + accessToken));
		resultActions.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoNotExist() throws Exception{
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions resultActions = mockMvc
				       .perform(delete("/products/{id}",nonExistingId)
				       .header("Authorization", "Bearer " + accessToken));
		
		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() throws Exception{
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions resultActions = mockMvc
				       .perform(delete("/products/{id}",dependentId)
					   .header("Authorization", "Bearer " + accessToken));
						resultActions.andExpect(status().isBadRequest());

	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions resultActions = mockMvc
				       .perform(put("/products/{id}",existingId)
					   .header("Authorization", "Bearer " + accessToken)
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
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		ResultActions resultActions = mockMvc
			       .perform(put("/products/{id}",nonExistingId)
				   .header("Authorization", "Bearer " + accessToken)
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
