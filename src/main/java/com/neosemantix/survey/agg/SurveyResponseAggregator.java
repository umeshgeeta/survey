// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.agg;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.neosemantix.survey.model.QuestionAggregate;
import com.neosemantix.survey.model.QuestionResponse;
import com.neosemantix.survey.model.SurveyAggregate;
import com.neosemantix.survey.model.SurveyDef;
import com.neosemantix.survey.model.SurveyQuestion;
import com.neosemantix.survey.model.SurveyResponse;

@Controller
public class SurveyResponseAggregator {

	public SurveyResponseAggregator() {
		// TODO Auto-generated constructor stub
	}
	
	public SurveyAggregate aggregateResponse(SurveyDef sd, SurveyAggregate sa, SurveyResponse sr) {
		SurveyAggregate result = sa;
		if (result != null) {
			// need to count the incoming response
			sa.setResponseCount(sa.getResponseCount() + 1);
			Map<String, QuestionAggregate> qas = sa.getQuesAggregates();
			if (qas == null) {
				// This can happen, when the first response is essentiall blank with no question aggregates.
				qas = new HashMap<>();
			}
			Map<String, QuestionResponse> responses = sr.getResponses();
			for (String qid: responses.keySet()) {
				QuestionAggregate qa = qas.get(qid);
				qa = processQuestionAggregare(qa, responses.get(qid));
				// Because incoming has an answer for the question for the first time while
				// no survey response before this had contained an answer to this question.
				// Hence we add back. In this case 'qa' at line before would be null.
				qas.put(qid, qa);
			}
			// Those questions for which there is no response in the incoming survey response;
			// will be left as is. 
			sa.setQuesAggregates(qas);
		} else {
			// We regard it as the first response.
		}
		return result;
	}
	
	private QuestionAggregate processQuestionAggregare(QuestionAggregate qa, QuestionResponse qr) {
		return null;
	}
	
	private Map<String, Boolean> isCompleteSurveyResponse(SurveyDef sd, SurveyResponse sr) {
		Map<String, Boolean> result = new HashMap<>();
		Map<Integer, SurveyQuestion> quesDef = sd.getQuestions();
		if (quesDef != null && !quesDef.isEmpty()) {
			Map<String, QuestionResponse> resps = sr.getResponses();
			for (Integer i: quesDef.keySet()) {
				SurveyQuestion sq = quesDef.get(i);
				QuestionResponse qr = resps.get(sq.getId());
				if (!isCompleteQuestionResponse(sq, qr)) {
					result.put(sq.getId(), false);
				}
			}
		}
		// else trivially false
		return result;
	}
	
	private boolean isCompleteQuestionResponse(SurveyQuestion sq, QuestionResponse qr) {
		boolean result = false;
		if (qr != null) {
			switch (sq.getType()) {
			// For multiple choice or multiple selection questions, we need at least one answer.
			case MULTIPLE_CHOICE:
			case MULTIPLE_SELECTION:
				if (qr.getChoiceSelections() != null && qr.getChoiceSelections().length != 0) {
					result = true;
				}
				break;
			case TEXT_RESPONSE:
				if (qr.getFreeResponse() != null && !qr.getFreeResponse().isEmpty()) {
					result = true;
				}
				break;
			}
		} 
		// else we regard it as false in any case
		return result;
	}
}
