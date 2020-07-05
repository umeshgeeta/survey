// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neosemantix.survey.model.SurveyResponse;
import com.neosemantix.survey.repo.SurveyResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SurveyResponseService {

	@Autowired
	private final SurveyResponseRepository srRepository;
	
	public SurveyResponseService(SurveyResponseRepository srr) {
		srRepository = srr;
	}
	
	public Mono<SurveyResponse> record(SurveyResponse sr){
		return srRepository.save(sr);
	}
	
	public Flux<SurveyResponse> getAll(String surveyDefId){
		// TBD
		return null;
	}

}
