// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Model is each survey definition has a unique id. Each survey definition 
 * comprises of a set of survey questions. For each survey definition, there
 * will be multiple survey responses associated as multiple users responds.
 */
@Document 
@Data 
public class SurveyDef {

	@Id 
    private String id;
	
	private String name;
	
	private String owner;
	
	private boolean editsAllowed;	 // default is false
	
	/**
	 * Order of question is important for display, so we use map.
	 * We start with order number 1, followed 2, 3 etc.
	 */
	private Map<Integer, SurveyQuestion> questions;
	
	public SurveyDef(String name, String owner) {
		this.name = name;
		this.owner = owner;
	}

}
