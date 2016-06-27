/*
 * Copyright 2016 Alexandr Evstigneev
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

package com.perl5.lang.perl.psi;

import com.intellij.psi.PsiElement;
import com.perl5.lang.perl.psi.utils.PerlContextType;
import org.jetbrains.annotations.Nullable;

/**
 * Created by hurricup on 17.02.2016.
 */
public interface PerlCompositeElement extends PsiElement
{
	/**
	 * Returns context type for psi element
	 *
	 * @return like scalar for scalars, strings and so on, lists for arrays, hashes, can be null if we not sure
	 */
	@Nullable
	PerlContextType getValueContextType();
}
