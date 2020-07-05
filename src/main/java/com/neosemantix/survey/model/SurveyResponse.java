// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.model;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SurveyResponse encapsulates answers provided a single user to a specific
 * survey. It has a reference to the survey definition to which these answers
 * apply to - id of that survey definition. 
 * 
 * Since each question has a distinct id (independent of id of the survey to
 * which that question belongs to); survey response is made up of question id
 * and the corresponding response. That map is the core attribute of this
 * entity.
 * 
 * The entity also tracks the timestamp when the response is persisted.
 */
@Document 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class SurveyResponse {

	@Id
	private String srid;
	
	/**
	 * Id of the survery definition to which this response belongs to.
	 */
	private String whichSurvey;
	
	private Date when;
	
	/**
	 * Name of the user / person who responds to the survey
	 */
	private String user;
	
	/**
	 * Map of question id and corresponding response
	 */
	private Map<String, QuestionResponse> responses;

}
