package com.connexience.server.model.logging.performance.policy;

import com.connexience.server.model.logging.performance.PerformanceModel;

import javax.persistence.EntityManager;

public class NewDataPolicy implements IRebuildPolicy {

    public boolean shouldRebuildModel(EntityManager em, PerformanceModel model)
    {
        // Is there enough new data to warrant a re-train?
        return true;
    }
}
