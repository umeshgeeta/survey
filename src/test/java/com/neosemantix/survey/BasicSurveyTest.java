// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.neosemantix.survey.model.SurveyDef;
import com.neosemantix.survey.model.SurveyQuestion;
import com.neosemantix.survey.model.SurveyQuestionType;
import com.neosemantix.survey.service.SurveyDefService;
import com.neosemantix.survey.service.SurveyResponseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@DataMongoTest 
@Import({SurveyDefService.class, SurveyResponseService.class})
public class BasicSurveyTest {
	
	private static String SurveyName = "Test Survey";
	private static String SurveyOwner = "Umesh";

	private final SurveyDefService surveyDefService;
	private final SurveyResponseService responseService;
	
	public BasicSurveyTest(@Autowired SurveyDefService sds,
			@Autowired SurveyResponseService srs) {
		this.surveyDefService = sds;
		this.responseService = srs;
	}
	
	@Test
	public void saveTestSurvy() {
		// We first eleminate earlier sample survey documents.
		surveyDefService.getAllByName(SurveyName, SurveyOwner).subscribe(next -> {
			// waiting to get this response is critical
			Mono<ServerResponse> dsd = surveyDefService.delete(next);
			// we need to ensure that we get the server response for delete confirmation
			System.out.println("Delete response is: "+ dsd.block().rawStatusCode() + " Deleted document is: " + next);
		});
		
		SurveyDef savedSd = surveyDefService.create(buildTestSurvey()).block();
		System.out.println(savedSd);
	}
	
	private SurveyDef buildTestSurvey() {
		String qstr = "How was your experience with this Webinar?";
		SurveyQuestion sq1 = new SurveyQuestion(SurveyQuestionType.MULTIPLE_CHOICE, qstr);
		Map<Integer, String> choices = new HashMap<>();
		choices.put(1, "Good");
		choices.put(2, "Ok");
		choices.put(3, "Bad");
		sq1.setChoices(choices);
		
		qstr = "Which areas you would like to see improvement? Select all applicable.";
		SurveyQuestion sq2 = new SurveyQuestion(SurveyQuestionType.MULTIPLE_SELECTION, qstr);
		choices.put(1, "User Interface");
		choices.put(2, "Performance and Responsiveness");
		choices.put(3, "Integration with Webinar");
		sq2.setChoices(choices);
		
		qstr = "Would you like to share any other feedback with us?";
		SurveyQuestion sq3 = new SurveyQuestion(SurveyQuestionType.TEXT_RESPONSE, qstr);
		
		Map<Integer, SurveyQuestion> qs = new HashMap<>();
		qs.put(1, sq1);
		qs.put(2, sq2);
		qs.put(3, sq3);
		
		SurveyDef sd = new SurveyDef(SurveyName, SurveyOwner);
		sd.setQuestions(qs);
		
		return sd;
	}

}
