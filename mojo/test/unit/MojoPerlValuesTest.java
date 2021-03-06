/*
 * Copyright 2015-2018 Alexandr Evstigneev
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

package unit;

import base.PerlLightTestCase;

public class MojoPerlValuesTest extends PerlLightTestCase {
  @Override
  protected String getTestDataPath() {
    return "testData/unit/perl/perlValues";
  }

  public void testAttributeConstantSub() {doTestMojoAttr();}

  public void testAttributeConstantValue() {doTestMojoAttr();}

  public void testAttributeFirstName() {doTestMojoAttr();}

  public void testAttributeSecondSub() {doTestMojoAttr();}

  public void testAttributeUnnamed() {doTestMojoAttr();}

  private void doTestMojoAttr() {
    myFixture.copyFileToProject("attributeDeclarations.pl");
    doTest();
  }

  public void testHelperValue() {
    myFixture.copyFileToProject("helperValueDeclaration.pl");
    doTest();
  }

  private void doTest() {
    doTestPerlValue();
  }
}
