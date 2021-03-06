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

package com.perl5.lang.perl.psi.mixins;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.*;
import com.perl5.lang.perl.psi.*;
import com.perl5.lang.perl.psi.impl.PerlCompositeElementImpl;
import com.perl5.lang.perl.psi.properties.PerlValuableEntity;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import com.perl5.lang.perl.util.PerlPackageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValues.UNKNOWN_VALUE;
import static com.perl5.lang.perl.lexer.PerlElementTypesGenerated.PACKAGE;
import static com.perl5.lang.perl.lexer.PerlElementTypesGenerated.QUALIFYING_PACKAGE;


public abstract class PerlMethodMixin extends PerlCompositeElementImpl implements PerlMethodCall {
  private static final Logger LOG = Logger.getInstance(PerlMethodMixin.class);

  public PerlMethodMixin(@NotNull ASTNode node) {
    super(node);
  }

  @Nullable
  public String getExplicitNamespaceName() {
    PerlNamespaceElement namespaceElement = getNamespaceElement();
    return namespaceElement != null ? namespaceElement.getCanonicalName() : null;
  }

  @Override
  public String getName() {
    PerlSubNameElement subNameElement = getSubNameElement();
    return subNameElement == null ? null : subNameElement.getText();
  }

  @NotNull
  @Override
  public PerlValue computePerlValue() {
    PerlValue subNameValue = PerlScalarValue.create(getName());
    if (subNameValue == UNKNOWN_VALUE) {
      return UNKNOWN_VALUE;
    }

    String explicitNamespaceName = getExplicitNamespaceName();
    boolean hasExplicitNamespace = StringUtil.isNotEmpty(explicitNamespaceName);
    PsiElement parentElement = getParent();
    boolean isNestedCall = parentElement instanceof PerlNestedCall;

    List<PerlValue> callArguments;
    if (parentElement instanceof PerlMethodContainer) {
      callArguments = ContainerUtil.map(((PerlMethodContainer)parentElement).getCallArgumentsList(), PerlValuesManager::from);
    }
    else {
      // these are sort and code variable
      LOG.debug("Non-method container container: " + parentElement.getClass().getSimpleName());
      callArguments = Collections.emptyList();
    }

    PsiElement derefExpression = parentElement.getParent();
    if (isNestedCall) {
      boolean isSuper = PerlPackageUtil.isSUPER(explicitNamespaceName);
      if (hasExplicitNamespace && !isSuper) { // ackward $var->Foo::Bar::method->
        return new PerlCallStaticValue(PerlScalarValue.create(explicitNamespaceName), subNameValue, callArguments, hasExplicitNamespace);
      }
      if (!(derefExpression instanceof PerlDerefExpression)) {
        LOG.warn("Expected deref expression, got " + derefExpression);
        return UNKNOWN_VALUE;
      }

      PsiElement previousValue = ((PerlDerefExpression)derefExpression).getPreviousElement(parentElement);
      if (previousValue == null) { // first in chain
        return new PerlCallStaticValue(
          PerlScalarValue.create(PerlPackageUtil.getContextNamespaceName(this)), subNameValue, callArguments, false);
      }
      else if (PerlPsiUtil.isSelfShortcut(previousValue)) {
        // fixme this need to be moved to respective values
        return new PerlCallObjectValue(
          PerlScalarValue.create(PerlPackageUtil.getContextNamespaceName(previousValue)),
          subNameValue, callArguments, isSuper);
      }
      else if (previousValue instanceof PerlValuableEntity) {
        PerlValue previousElementValue = PerlValuesManager.from(previousValue);
        return new PerlCallObjectValue(
          previousElementValue == UNKNOWN_VALUE ? PerlPackageUtil.UNIVERSAL_VALUE : previousElementValue, subNameValue, callArguments,
          isSuper);
      }
    }
    else if (isObjectMethod() && hasExplicitNamespace) {
      return new PerlCallObjectValue(PerlScalarValue.create(explicitNamespaceName), subNameValue, callArguments, false);
    }
    else if (hasExplicitNamespace) {
      return new PerlCallStaticValue(PerlScalarValue.create(explicitNamespaceName), subNameValue, callArguments, hasExplicitNamespace);
    }
    else {
      return new PerlCallStaticValue(
        PerlScalarValue.create(PerlPackageUtil.getContextNamespaceName(this)), subNameValue, callArguments, hasExplicitNamespace);
    }

    return UNKNOWN_VALUE;
  }

  @Override
  public boolean isObjectMethod() {
    boolean hasExplicitNamespace = hasExplicitNamespace();
    boolean isNestedCall = getParent() instanceof PerlNestedCall;

    return !hasExplicitNamespace && isNestedCall            // part of ..->method()
           || hasExplicitNamespace && getFirstChild() instanceof PerlSubNameElement    // method Foo::Bar
           || hasExplicitNamespace        // SUPER::method
              && isNestedCall
              && getFirstChild() instanceof PerlNamespaceElement
              && ((PerlNamespaceElement)getFirstChild()).isSUPER()
      ;
  }

  @Nullable
  @Override
  public PerlNamespaceElement getNamespaceElement() {
    PsiElement childByType = findChildByType(QUALIFYING_PACKAGE);
    if (childByType == null) {
      childByType = findChildByType(PACKAGE);
    }
    return childByType == null ? null : (PerlNamespaceElement)childByType;
  }

  @Nullable
  @Override
  public PerlSubNameElement getSubNameElement() {
    return findChildByClass(PerlSubNameElement.class);
  }
}
