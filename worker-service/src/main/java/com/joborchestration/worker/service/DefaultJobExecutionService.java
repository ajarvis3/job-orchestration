package com.joborchestration.worker.service;

import com.joborchestration.jobdefinition.JobEvent;
import com.joborchestration.jobdefinition.JobEventType;
import com.joborchestration.jobdefinition.JobInstance;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
class DefaultJobExecutionService implements JobExecutionService {

    @Override
    public JobEvent execute(JobInstance jobInstance) {
        Map<String, Object> outputParams = new LinkedHashMap<>();
        outputParams.put("jobType", jobInstance.jobType());
        outputParams.put("receivedParams", jobInstance.params());
        return new JobEvent(JobEventType.SUCCESS, jobInstance.id(), outputParams);
    }
}
