package com.lucasdev.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasdev.dscatalog.dto.ProductDTO;
import com.lucasdev.dscatalog.tests.Factory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception{
		ResultActions resultActions = 
				mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
				       .accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		resultActions.andExpect(jsonPath("$.content").exists());
		resultActions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		resultActions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		resultActions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
		
		ProductDTO productDTO = Factory.createProducDTO();
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();
		
		ResultActions resultActions = mockMvc
				       .perform(put("/products/{id}",existingId)
				       .content(jsonBody)
				       .contentType(MediaType.APPLICATION_JSON)
				       .accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").value(existingId));
		resultActions.andExpect(jsonPath("$.name").value(expectedName));
		resultActions.andExpect(jsonPath("$.description").value(expectedDescription));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
		
		ProductDTO productDTO = Factory.createProducDTO();
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions resultActions = mockMvc
				       .perform(put("/products/{id}",nonExistingId)
				       .content(jsonBody)
				       .contentType(MediaType.APPLICATION_JSON)
				       .accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().isNotFound());
		
	}
}
