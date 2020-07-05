// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * We allow only one single submission per user, which cab be optionally edited.
 * 
 * Model is each survey definition has a unique id. Each survey definition 
 * comprises of a set of survey questions. Each survey question has a unique id.
 * 
 * For each survey definition, there will be multiple survey responses associated
 * as multiple users responds.
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
	 */
	private Map<Integer, SurveyQuestion> questions;
	
	public SurveyDef(String name, String owner) {
		this.name = name;
		this.owner = owner;
	}

}
