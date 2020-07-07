// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import com.neosemantix.survey.model.SurveyResponse;

public interface SurveyResponseListener {

	void onSurveyResponse(SurveyResponse chunk);

	void processComplete();
}
