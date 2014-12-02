package org.chain.model;

import java.util.Collection;

public class TestWrapper
{
    private Collection<TestClass> testClasses;

    public TestWrapper(Collection<TestClass> testClasses)
    {
        setTestClasses(testClasses);
    }

    public Collection<TestClass> getTestClasses() {
        return testClasses;
    }

    public void setTestClasses(Collection<TestClass> testClasses) {
        this.testClasses = testClasses;
    }
}
