package com.connexience.server.model.logging.performance.policy;

import com.connexience.server.model.logging.performance.PerformanceModel;

import javax.persistence.EntityManager;


public class AlwaysRebuildPolicy implements IRebuildPolicy {

    public boolean shouldRebuildModel(EntityManager em, PerformanceModel model)
    {
        return true;
    }
}
