/*
 * Copyright 2015-2019 Alexandr Evstigneev
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

package com.perl5.lang.perl.parser.perlswitch;

import com.intellij.psi.tree.IElementType;
import com.perl5.lang.perl.parser.elementTypes.PerlElementTypeEx;
import com.perl5.lang.perl.parser.perlswitch.psi.*;


public interface PerlSwitchElementTypes {
  IElementType SWITCH_COMPOUND = new PerlElementTypeEx("SWITCH_COMPOUND", PerlSwitchCompoundStatementImpl.class) ;
  IElementType SWITCH_CONDITION = new PerlElementTypeEx("SWITCH_CONDITION", PerlSwitchConditionImpl.class);
  IElementType CASE_COMPOUND = new PerlElementTypeEx("CASE_COMPOUND", PerlCaseCompoundStatementImpl.class);
  IElementType CASE_DEFAULT = new PerlElementTypeEx("CASE_DEFAULT", PerlCaseDefaultCompoundImpl.class);
  IElementType CASE_CONDITION = new PerlElementTypeEx("CASE_CONDITION", PerlCaseConditionImpl.class);
}
