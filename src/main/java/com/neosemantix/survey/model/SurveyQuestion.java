// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document 
@Data 
public class SurveyQuestion {

	@Id
	private String id;
	
	private SurveyQuestionType type;
	
	private String question;
	
	/**
	 * Choice number and associated choice text.
	 */
	private Map<Integer, String> choices;
	
	/**
	 * Needed for making an object back when we read from MongoDB.
	 */
	public SurveyQuestion() {
		
	}
	
	public SurveyQuestion(SurveyQuestionType sqt, String q) {
		type = sqt;
		question = q;
	}
	
	public SurveyQuestion(String sqt, String q) {
		this(SurveyQuestionType.convert(sqt),q);
	}

}
