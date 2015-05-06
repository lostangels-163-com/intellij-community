/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.execution.junit;

import com.intellij.junit4.SMTestSender;
import com.intellij.openapi.util.text.StringUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JUnitTreeByDescriptionHierarchyTest {
  @Test
  public void testEmptySuite() throws Exception {
    doTest(Description.createSuiteDescription("empty suite"), "");
  }

  @Test
  public void test2Parameterized() throws Exception {
    final Description root = Description.createSuiteDescription("root");
    final ArrayList<Description> tests = new ArrayList<Description>();
    for (String className : new String[]{"a.TestA", "a.TestB"}) {
      final Description aTestClass = Description.createSuiteDescription(className);
      root.addChild(aTestClass);
      attachParameterizedTests(className, aTestClass, tests);
    }
    doTest(root, tests,
           "##teamcity[suiteTreeStarted name='TestA' locationHint='java:suite://a.TestA']\n" +
           "##teamcity[suiteTreeStarted name='|[0|]' locationHint='java:suite://a.TestA.|[0|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[0|]' locationHint='java:test://a.TestA.testName|[0|]']\n" +
           "##teamcity[suiteTreeEnded name='|[0|]']\n" +
           "##teamcity[suiteTreeStarted name='|[1|]' locationHint='java:suite://a.TestA.|[1|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[1|]' locationHint='java:test://a.TestA.testName|[1|]']\n" +
           "##teamcity[suiteTreeEnded name='|[1|]']\n" +
           "##teamcity[suiteTreeEnded name='TestA']\n" +
           "##teamcity[suiteTreeStarted name='TestB' locationHint='java:suite://a.TestB']\n" +
           "##teamcity[suiteTreeStarted name='|[0|]' locationHint='java:suite://a.TestB.|[0|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[0|]' locationHint='java:test://a.TestB.testName|[0|]']\n" +
           "##teamcity[suiteTreeEnded name='|[0|]']\n" +
           "##teamcity[suiteTreeStarted name='|[1|]' locationHint='java:suite://a.TestB.|[1|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[1|]' locationHint='java:test://a.TestB.testName|[1|]']\n" +
           "##teamcity[suiteTreeEnded name='|[1|]']\n" +
           "##teamcity[suiteTreeEnded name='TestB']\n",


           "##teamcity[enteredTheMatrix]\n" +
           "##teamcity[rootName name = 'root']\n" +
           "##teamcity[testSuiteFinished name='root']\n" +
           "##teamcity[testSuiteStarted name ='TestA']\n" +
           "##teamcity[testSuiteStarted name ='|[0|]']\n" +
           "##teamcity[testStarted name='testName|[0|]' locationHint='java:test://a.TestA.testName|[0|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[0|]']\n" +
           "##teamcity[testSuiteFinished name='|[0|]']\n" +
           "##teamcity[testSuiteStarted name ='|[1|]']\n" +
           "##teamcity[testStarted name='testName|[1|]' locationHint='java:test://a.TestA.testName|[1|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[1|]']\n" +
           "##teamcity[testSuiteFinished name='|[1|]']\n" +
           "##teamcity[testSuiteFinished name='TestA']\n" +
           "##teamcity[testSuiteStarted name ='TestB']\n" +
           "##teamcity[testSuiteStarted name ='|[0|]']\n" +
           "##teamcity[testStarted name='testName|[0|]' locationHint='java:test://a.TestB.testName|[0|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[0|]']\n" +
           "##teamcity[testSuiteFinished name='|[0|]']\n" +
           "##teamcity[testSuiteStarted name ='|[1|]']\n" +
           "##teamcity[testStarted name='testName|[1|]' locationHint='java:test://a.TestB.testName|[1|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[1|]']\n" +
           "##teamcity[testSuiteFinished name='|[1|]']\n" +
           "##teamcity[testSuiteFinished name='TestB']\n");
  }

  @Test
  public void testSingleParameterizedClass() throws Exception {
    final String className = "a.TestA";
    final Description aTestClassDescription = Description.createSuiteDescription(className);
    final ArrayList<Description> tests = new ArrayList<Description>();
    attachParameterizedTests(className, aTestClassDescription, tests);
    doTest(aTestClassDescription, tests,
           //tree
           "##teamcity[suiteTreeStarted name='|[0|]' locationHint='java:suite://a.TestA.|[0|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[0|]' locationHint='java:test://a.TestA.testName|[0|]']\n" +
           "##teamcity[suiteTreeEnded name='|[0|]']\n" +
           "##teamcity[suiteTreeStarted name='|[1|]' locationHint='java:suite://a.TestA.|[1|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[1|]' locationHint='java:test://a.TestA.testName|[1|]']\n" +
           "##teamcity[suiteTreeEnded name='|[1|]']\n",
           //start
           "##teamcity[enteredTheMatrix]\n" +
           "##teamcity[rootName name = 'TestA' comment = 'a']\n" +
           "##teamcity[testSuiteStarted name ='|[0|]']\n" +
           "##teamcity[testStarted name='testName|[0|]' locationHint='java:test://a.TestA.testName|[0|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[0|]']\n" +
           "##teamcity[testSuiteFinished name='|[0|]']\n" +
           "##teamcity[testSuiteStarted name ='|[1|]']\n" +
           "##teamcity[testStarted name='testName|[1|]' locationHint='java:test://a.TestA.testName|[1|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[1|]']\n" +
           "##teamcity[testSuiteFinished name='|[1|]']\n" +
           "##teamcity[testSuiteFinished name='TestA']\n");
  }

  @Test
  public void test2SuitesWithTheSameTest() throws Exception {
    final Description root = Description.createSuiteDescription("root");
    final String className = "ATest";
    final String methodName = "test1";
    final List<Description> tests = new ArrayList<Description>();
    for( String suiteName : new String[] {"ASuite1", "ASuite2"}) {
      final Description aSuite = Description.createSuiteDescription(suiteName);
      root.addChild(aSuite);
      final Description aTest = Description.createSuiteDescription(className);
      aSuite.addChild(aTest);
      final Description testDescription = Description.createTestDescription(className, methodName);
      tests.add(testDescription);
      aTest.addChild(testDescription);
    }

    doTest(root, tests,
           //expected tree
           "##teamcity[suiteTreeStarted name='ASuite1' locationHint='java:suite://ASuite1']\n" +
           "##teamcity[suiteTreeStarted name='ATest' locationHint='java:suite://ATest']\n" +
           "##teamcity[suiteTreeNode name='test1' locationHint='java:test://ATest.test1']\n" +
           "##teamcity[suiteTreeEnded name='ATest']\n" +
           "##teamcity[suiteTreeEnded name='ASuite1']\n" +
           "##teamcity[suiteTreeStarted name='ASuite2' locationHint='java:suite://ASuite2']\n" +
           "##teamcity[suiteTreeStarted name='ATest' locationHint='java:suite://ATest']\n" +
           "##teamcity[suiteTreeNode name='test1' locationHint='java:test://ATest.test1']\n" +
           "##teamcity[suiteTreeEnded name='ATest']\n" +
           "##teamcity[suiteTreeEnded name='ASuite2']\n",

           //started
           "##teamcity[enteredTheMatrix]\n" +
           "##teamcity[rootName name = 'root']\n" +
           "##teamcity[testSuiteFinished name='root']\n" +
           "##teamcity[testSuiteStarted name ='ASuite1']\n" +
           "##teamcity[testSuiteStarted name ='ATest']\n" +
           "##teamcity[testStarted name='test1' locationHint='java:test://ATest.test1']\n" +
           "\n" +
           "##teamcity[testFinished name='test1']\n" +
           "##teamcity[testSuiteFinished name='ATest']\n" +
           "##teamcity[testSuiteFinished name='ASuite1']\n" +
           "##teamcity[testSuiteStarted name ='ASuite2']\n" +
           "##teamcity[testSuiteStarted name ='ATest']\n" +
           "##teamcity[testStarted name='test1' locationHint='java:test://ATest.test1']\n" +
           "\n" +
           "##teamcity[testFinished name='test1']\n" +
           "##teamcity[testSuiteFinished name='ATest']\n" +
           "##teamcity[testSuiteFinished name='ASuite2']\n");
  }

  private static void doTest(Description root, List<Description> tests, String expectedTree, String expectedStart) throws Exception {
    final StringBuffer buf = new StringBuffer();
    final SMTestSender sender = new SMTestSender(new PrintStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        buf.append(new String(new byte[]{(byte)b}));
      }
    }));
    sender.sendTree(root);

    Assert.assertEquals("output: " + buf, expectedTree, StringUtil.convertLineSeparators(buf.toString()));

    buf.setLength(0);

    sender.testRunStarted(root);
    for (Description test : tests) {
      sender.testStarted(test);
      sender.testFinished(test);
    }
    sender.testRunFinished(new Result());

    Assert.assertEquals("output: " + buf, expectedStart, StringUtil.convertLineSeparators(buf.toString()));
  }

  @Test
  public void testSingleMethod() throws Exception {
    final Description rootDescription = Description.createTestDescription("TestA", "testName");
    doTest(rootDescription, "##teamcity[suiteTreeNode name='testName' locationHint='java:test://TestA.testName']\n");
  }

  @Test
  public void testSuiteAndParameterizedTestsInOnePackage() throws Exception {
    final Description root = Description.createSuiteDescription("root");
    final Description aTestClass = Description.createSuiteDescription("ATest");
    root.addChild(aTestClass);
    final ArrayList<Description> tests = new ArrayList<Description>();
    attachParameterizedTests("ATest", aTestClass, tests);
    final Description suiteDescription = Description.createSuiteDescription("suite");
    root.addChild(suiteDescription);
    final Description aTestClassWithJUnit3Test = Description.createSuiteDescription("ATest");
    suiteDescription.addChild(aTestClassWithJUnit3Test);
    final Description testDescription = Description.createTestDescription("ATest", "test");
    aTestClassWithJUnit3Test.addChild(testDescription);
    tests.add(testDescription);
    doTest(root, tests,
           "##teamcity[suiteTreeStarted name='ATest' locationHint='java:suite://ATest']\n" +
           "##teamcity[suiteTreeStarted name='|[0|]' locationHint='java:suite://ATest.|[0|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[0|]' locationHint='java:test://ATest.testName|[0|]']\n" +
           "##teamcity[suiteTreeEnded name='|[0|]']\n" +
           "##teamcity[suiteTreeStarted name='|[1|]' locationHint='java:suite://ATest.|[1|]']\n" +
           "##teamcity[suiteTreeNode name='testName|[1|]' locationHint='java:test://ATest.testName|[1|]']\n" +
           "##teamcity[suiteTreeEnded name='|[1|]']\n" +
           "##teamcity[suiteTreeEnded name='ATest']\n" +
           "##teamcity[suiteTreeStarted name='suite' locationHint='java:suite://suite']\n" +
           "##teamcity[suiteTreeStarted name='ATest' locationHint='java:suite://ATest']\n" +
           "##teamcity[suiteTreeNode name='test' locationHint='java:test://ATest.test']\n" +
           "##teamcity[suiteTreeEnded name='ATest']\n" +
           "##teamcity[suiteTreeEnded name='suite']\n",

           //start
           "##teamcity[enteredTheMatrix]\n" +
           "##teamcity[rootName name = 'root']\n" +
           "##teamcity[testSuiteFinished name='root']\n" +
           "##teamcity[testSuiteStarted name ='ATest']\n" +
           "##teamcity[testSuiteStarted name ='|[0|]']\n" +
           "##teamcity[testStarted name='testName|[0|]' locationHint='java:test://ATest.testName|[0|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[0|]']\n" +
           "##teamcity[testSuiteFinished name='|[0|]']\n" +
           "##teamcity[testSuiteStarted name ='|[1|]']\n" +
           "##teamcity[testStarted name='testName|[1|]' locationHint='java:test://ATest.testName|[1|]']\n" +
           "\n" +
           "##teamcity[testFinished name='testName|[1|]']\n" +
           "##teamcity[testSuiteFinished name='|[1|]']\n" +
           "##teamcity[testSuiteFinished name='ATest']\n" +
           "##teamcity[testSuiteStarted name ='suite']\n" +
           "##teamcity[testSuiteStarted name ='ATest']\n" +
           "##teamcity[testStarted name='test' locationHint='java:test://ATest.test']\n" +
           "\n" +
           "##teamcity[testFinished name='test']\n" +
           "##teamcity[testSuiteFinished name='ATest']\n" +
           "##teamcity[testSuiteFinished name='suite']\n");
  }


  private static void attachParameterizedTests(String className, Description aTestClass, List<Description> tests) {
    for (String paramName : new String[]{"[0]", "[1]"}) {
      final Description param1 = Description.createSuiteDescription(paramName);
      aTestClass.addChild(param1);
      final Description testDescription = Description.createTestDescription(className, "testName" + paramName);
      tests.add(testDescription);
      param1.addChild(testDescription);
    }
  }
  
  private static void doTest(Description description, String expected) {
    final StringBuffer buf = new StringBuffer();
    new SMTestSender(new PrintStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        buf.append(new String(new byte[]{(byte)b}));
      }
    })).sendTree(description);

    Assert.assertEquals("output: " + buf, expected, StringUtil.convertLineSeparators(buf.toString()));
  }

  @Test
  public void testProcessEmptyTestCase() throws Exception {
    final Description description = Description.createSuiteDescription("TestA");
    final Description emptyDescription = Description.createTestDescription(SMTestSender.EMPTY_SUITE_NAME, SMTestSender.EMPTY_SUITE_WARNING);
    description.addChild(emptyDescription);
    doTest(description, Collections.singletonList(emptyDescription),
           "##teamcity[suiteTreeNode name='warning' locationHint='java:test://TestA.warning']\n",


           "##teamcity[enteredTheMatrix]\n" +
           "##teamcity[rootName name = 'TestA']\n" +
           "##teamcity[testStarted name='warning' locationHint='java:test://junit.framework.TestSuite$1.warning']\n" +
           "\n" +
           "##teamcity[testFinished name='warning']\n" +
           "##teamcity[testSuiteFinished name='TestA']\n");
  }
}
