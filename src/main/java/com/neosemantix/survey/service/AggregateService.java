// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neosemantix.survey.agg.SurveyResponseAggregator;
import com.neosemantix.survey.model.SurveyAggregate;
import com.neosemantix.survey.model.SurveyResponse;
import com.neosemantix.survey.repo.AggregateRepository;

import reactor.core.publisher.Mono;

@Service
public class AggregateService {
	
	@Autowired
	private final AggregateRepository aggRepository;
	
	@Autowired
	private final SurveyResponseAggregator srAgg;

	public AggregateService(AggregateRepository ar, SurveyResponseAggregator sra) {
		aggRepository = ar;
		srAgg = sra;
	}
	
	public Mono<SurveyAggregate> findAggBySurvyDefId(String sfId){
		return aggRepository.findBySurveyDefId(sfId);
	}
	
	public Mono<SurveyAggregate> save(SurveyAggregate sa) {
		return aggRepository.save(sa);
	}
	
	public void processNewSurveyResponse(SurveyResponse sr) {
		Mono<SurveyAggregate> existingAgg = findAggBySurvyDefId(sr.getSurveyId());
		
	}

}
