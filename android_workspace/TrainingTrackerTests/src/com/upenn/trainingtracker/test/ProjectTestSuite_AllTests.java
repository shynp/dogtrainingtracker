package com.upenn.trainingtracker.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

public class ProjectTestSuite_AllTests extends TestSuite
{
    public static Test suite () {
        return new TestSuiteBuilder(ProjectTestSuite_AllTests.class)
            .includeAllPackagesUnderHere()
            .build();
    }
}