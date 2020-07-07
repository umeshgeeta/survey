// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import com.neosemantix.survey.model.SurveyResponse;

public interface SurveyResponseProcessor {
	
	void register(SurveyResponseListener listener);
	
	void processSurveyResponse(SurveyResponse msr);

}
