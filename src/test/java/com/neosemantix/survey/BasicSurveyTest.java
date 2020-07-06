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

	private static SurveyDef savedSd;
	
	public BasicSurveyTest(@Autowired SurveyDefService sds,
			@Autowired SurveyResponseService srs) {
		this.surveyDefService = sds;
		this.responseService = srs;
	}
	
	@Test
	@Order(1)
	public void saveTestSurvy() {
		Set<String> deleteSurveyDefIds = new HashSet<>();
		// We first eleminate earlier sample survey documents.
		Flux<SurveyDef> fluxsd = surveyDefService.getAllByName(SurveyName, SurveyOwner);
		fluxsd.subscribe(next -> {
			if (next != null) {
				deleteSurveyDefIds.add(next.getId());
				// waiting to get this response is critical
				Mono<ServerResponse> dsd = surveyDefService.delete(next);
				int status = dsd.block().rawStatusCode();
				// we need to ensure that we get the server response for delete confirmation
				System.out.println("SurveyDef delete status is: "+ status + " Deleted document is: " + next);
			}
		});
		fluxsd.blockLast();
		System.out.println("Deleted survey defs are: " + deleteSurveyDefIds);
		boolean responsesDeleted = false;
		for (String sdid: deleteSurveyDefIds) {
			System.out.println("Will search responses for survey def: " + sdid);
			Flux<SurveyResponse> responses = responseService.getAll(sdid);
			
			SurveyResponse firstResponse = responses.blockFirst();
			SurveyResponse lastResponse = responses.blockLast();
			if (firstResponse != null) {
				// we delete all survey responses associated with this survey as well
				Mono<ServerResponse> sr = responseService.delete(firstResponse);
				int status = sr.block().rawStatusCode();
				System.out.println("SurveyResponse delete status is: "+ status + " Deleted SurveyResponse is: " + firstResponse);
				if (lastResponse != null) {
					sr = responseService.delete(lastResponse);
					status = sr.block().rawStatusCode();
					System.out.println("SurveyResponse delete status is: "+ status + " Deleted SurveyResponse is: " + lastResponse);
					responsesDeleted = true;
				} else {
					responsesDeleted = true;
				}
			}  else {
				responsesDeleted = true;
			}
		}
		while (!responsesDeleted) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		savedSd = surveyDefService.create(buildTestSurvey()).block();
		System.out.println("Saved Survey Def is: " +savedSd);
	}
	
	@Test
	@Order(2)
	public void recordResponses() {	
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
