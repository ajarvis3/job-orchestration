package com.joborchestration.worker.service;

import com.joborchestration.jobdefinition.JobEvent;
import com.joborchestration.jobdefinition.JobInstance;

public interface JobExecutionService {

    JobEvent execute(JobInstance jobInstance);
}

