package com.connexience.server.model.logging.performance.policy;

import com.connexience.server.model.logging.performance.PerformanceModel;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: nsjw7
 * Date: 30/04/2013
 * Time: 13:40
 * To change this template use File | Settings | File Templates.
 */
public interface IRebuildPolicy {
    public boolean shouldRebuildModel(EntityManager em, PerformanceModel model);

}
