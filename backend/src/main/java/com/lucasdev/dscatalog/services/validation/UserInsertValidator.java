package com.lucasdev.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.lucasdev.dscatalog.dto.UserInsertDTO;
import com.lucasdev.dscatalog.entities.User;
import com.lucasdev.dscatalog.repositories.UserRepository;
import com.lucasdev.dscatalog.resources.exceptions.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

		User user = userRepository.findByEmail(dto.getEmail());
		
		   if(user != null) {
			   list.add(new FieldMessage("email", "Email já utilizado"));
		   }
		
		
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
