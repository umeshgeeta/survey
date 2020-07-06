// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.neosemantix.survey.model.QuestionResponse;
import com.neosemantix.survey.model.SurveyDef;
import com.neosemantix.survey.model.SurveyQuestion;
import com.neosemantix.survey.model.SurveyQuestionType;
import com.neosemantix.survey.model.SurveyResponse;
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
	
	private static Set<SurveyDef> deletedSurveyDef;
	private static SurveyDef savedSd;
	
	public BasicSurveyTest(@Autowired SurveyDefService sds,
			@Autowired SurveyResponseService srs) {
		this.surveyDefService = sds;
		this.responseService = srs;
	}
	
	@Test
	@Order(1)
	public void saveTestSurvy() {
		deletedSurveyDef = new HashSet<>();
		// We first eleminate earlier sample survey documents.
		surveyDefService.getAllByName(SurveyName, SurveyOwner).subscribe(next -> {
			// waiting to get this response is critical
			Mono<ServerResponse> dsd = surveyDefService.delete(next);
			// we need to ensure that we get the server response for delete confirmation
			System.out.println("Delete response is: "+ dsd.block().rawStatusCode() + " Deleted document is: " + next);
			deletedSurveyDef.add(next);
			// we delete all survey responses associated with this survey as well
			responseService.getAll(next.getId()).subscribe(resp -> {
				Mono<ServerResponse> sr = responseService.delete(resp);
				System.out.println("Delete response: "+ sr.block().rawStatusCode() + " Deleted response is: " + resp);
			});
		});
		
		savedSd = surveyDefService.create(buildTestSurvey()).block();
		System.out.println(savedSd);
	}
	
	@Test
	@Order(2)
	public void recordResponses() {
		if (deletedSurveyDef != null && !deletedSurveyDef.isEmpty()) {
			int howManySurveys = deletedSurveyDef.size();
			for (SurveyDef sd: deletedSurveyDef) {
				this.responseService.getAll(sd.getId()).subscribe(sr -> {
					Mono<ServerResponse> dsd = responseService.delete(sr);
					System.out.println("Delete response is: "+ dsd.block().rawStatusCode() + " Deleted response is: " + sr);
				});
			}
			System.out.println("Deleted responses for " + howManySurveys);
		} else {
			System.out.println("Did not find any survey responses to delete");
		}
		
		SurveyResponse[] sdrs = buildTestSurveyResponse(savedSd);
		SurveyResponse sr0 = responseService.record(sdrs[0]).block();
		System.out.println("Saved: " + sr0);
		SurveyResponse sr1 = responseService.record(sdrs[1]).block();
		System.out.println("Saved: " + sr1);
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
	
	private SurveyResponse[]  buildTestSurveyResponse(SurveyDef sd) {
		SurveyResponse[] result = new SurveyResponse[2];
		int[] qr1 = {1};
		int[] qr2 = {1,3};
		result[0] = buildSingleSurveyResponse(sd, "User1", qr1, qr2, "Overall I had satisfactory Webinar experience.");
		
		int[] qr21 = {2};
		int[] qr22 = {2,3};
		result[1] = buildSingleSurveyResponse(sd, "User2", qr21, qr22, "Not a very happy Webinar experience.");

		return result;
	}
	
	private SurveyResponse buildSingleSurveyResponse(SurveyDef sd, String user, int[] qra, int[] qr2, String resp) {
		Map<Integer, SurveyQuestion> sdQuestions = sd.getQuestions();
		SurveyResponse sr1 = new SurveyResponse();
		sr1.setSurveyId(sd.getId());
		sr1.setWhen(new Date());
		sr1.setUser(user);
		Map<String, QuestionResponse> qrs = new HashMap<>();
		
		SurveyQuestion firstQ = sdQuestions.get(1);
		QuestionResponse qr11 = new QuestionResponse();
		qr11.setChoiceSelections(qra);
		qrs.put(firstQ.getId(), qr11);
		
		SurveyQuestion secondQ = sdQuestions.get(2);
		QuestionResponse qr12 = new QuestionResponse();
		qr12.setChoiceSelections(qr2);
		qrs.put(secondQ.getId(), qr12);
		
		SurveyQuestion thirdQ = sdQuestions.get(3);
		QuestionResponse qr13 = new QuestionResponse();
		qr13.setFreeResponse(resp);
		qrs.put(thirdQ.getId(), qr13);
		
		sr1.setResponses(qrs);
		
		return sr1;
	}

}
