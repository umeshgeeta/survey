This is a survey application implemented using Spring Boot JavaRx and MongoDB. Use case is we install Mongo DB locally and run this Spring Boot Application on the machine so that other Web Application running on that VM is able to utilize Survey Capabilities. There are 3 main parts to the functionality:

- To define and save Survey Definitions which comprises of Survey Questions.
- To record a survery response.
- To aggregate survey reponses.

Current code focuses on using Spring Framrework Reactor techniquen (Flux and Mono) to store Survey Definition and Survey Responses. Work in progress is about aggreation followed by Rest API controller. End goal is to make API end points available so that UI can be developed subsequently.


