package com.gwtproject;

import com.gwtproject.client.SortingTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class SortingSuite extends GWTTestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for Sorting");
    suite.addTestSuite(SortingTest.class);
    return suite;
  }
}
