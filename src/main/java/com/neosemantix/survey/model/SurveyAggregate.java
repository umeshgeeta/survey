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
public class SurveyAggregate {

	private String surveyDefId;
	
	private int responseCount;
	
	/**
	 * Number of responses which have answered all questions. Note that for 
	 * 'free text' question, the answer should be non-blank, non-null.
	 */
	private int completeResoponses;
	
	/**
	 * Id of the question and associted aggregate computed so far.
	 */
	private Map<String, QuestionAggregate> quesAggregates;

}
