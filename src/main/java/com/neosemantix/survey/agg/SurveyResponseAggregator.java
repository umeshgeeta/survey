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
import com.neosemantix.survey.model.SurveyQuestionType;
import com.neosemantix.survey.model.SurveyResponse;

@Controller
public class SurveyResponseAggregator {

	public SurveyResponseAggregator() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sd
	 * @param sa
	 * @param sr SurveyResponse is expected to contain only non-null / non-empty responses.
	 * @return
	 */
	public SurveyAggregate aggregateResponse(SurveyDef sd, SurveyAggregate sa, SurveyResponse sr) {
		SurveyAggregate result = sa;
		if (result == null) {
			// this is the first response to this survey
			result = new SurveyAggregate();
			result.setSurveyDefId(sd.getId());
		}
		// need to count the incoming response
		result.setResponseCount(result.getResponseCount() + 1);
		Map<String, QuestionAggregate> qas = result.getQuesAggregates();
		if (qas == null) {
			// This can happen, when the first response is essentiall blank with no question aggregates.
			qas = new HashMap<>();
		}
		Map<String, SurveyQuestion> qidMap = sd.getQuestionIdMap();
		Map<String, QuestionResponse> responses = sr.getResponses();
		for (String qid: responses.keySet()) {
			SurveyQuestion sq = qidMap.get(qid);
			QuestionAggregate qa = qas.get(qid);
			qa = aggregateQuestionResponse(sq.getType(), qa, responses.get(qid));
			// Because incoming has an answer for the question for the first time while
			// no survey response before this had contained an answer to this question.
			// Hence we add back. In this case 'qa' at line before would be null.
			qas.put(qid, qa);
		}
		// Those questions for which there is no response in the incoming survey response;
		// will be left as is. 
		result.setQuesAggregates(qas);
		if (qidMap.size() == responses.size()) {
			result.setCompleteResoponses(result.getCompleteResoponses()+1);
		}
		return result;
	}
	
	private QuestionAggregate aggregateQuestionResponse(SurveyQuestionType sqt, QuestionAggregate qa, QuestionResponse qr) {
		// let us assume that qr is non-null and valid
		if (qa == null) {
			qa = new QuestionAggregate();
		}
		qa.setRespCount(qa.getRespCount()+1);
		if (sqt != SurveyQuestionType.TEXT_RESPONSE) {
			Map<Integer, Integer> cs = qa.getChoiceCount();
			if (cs == null) {
				// this is the first time response
				cs = new HashMap<>();
			}
			int[] selection = qr.getChoiceSelections();
			for (int i=0; i<selection.length; i++) {
				Integer cv = cs.get(i);
				if (cv == null) {
					cs.put(cv, 1);
				} else {
					cs.put(selection[i], cv+1);
				}
			}
		} 
		// else nothing to do; response is already validate as non-null and not empty
		return qa;
	}
}
