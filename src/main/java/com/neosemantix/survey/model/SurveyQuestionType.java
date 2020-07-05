// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.model;

public enum SurveyQuestionType {
	
	MULTIPLE_CHOICE,
	
	MULTIPLE_SELECTION,
	
	TEXT_RESPONSE;
	
	public static SurveyQuestionType convert(String sqt) {
		switch (sqt) {
		case "MULTIPLE_CHOICE":	
			return SurveyQuestionType.MULTIPLE_CHOICE;
		case "MULTIPLE_SELECTION":	
			return SurveyQuestionType.MULTIPLE_SELECTION ;
		case "TEXT_RESPONSE":	
			return SurveyQuestionType.TEXT_RESPONSE ; 
			default:
				throw new RuntimeException("Unknown question type: " + sqt);
		}
	}

}
