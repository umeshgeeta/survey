// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.model;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAggregate {

	/**
	 * Count of responses received for this question
	 */
	private int respCount;
	
	/**
	 * For each choice, number of responses which selected that choice.
	 */
	private Map<Integer, Integer> choiceCount;

}
