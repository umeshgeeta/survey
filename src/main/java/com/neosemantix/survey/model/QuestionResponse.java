// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
	
	/**
	 * Single choice selection or multiple, array of selected choice numbers.
	 */
	private int[] choiceSelections;
	
	private String freeResponse;
}
