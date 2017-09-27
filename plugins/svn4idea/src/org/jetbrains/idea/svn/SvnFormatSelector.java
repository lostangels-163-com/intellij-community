/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
package org.jetbrains.idea.svn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.svn.svnkit.SvnKitManager;

import java.io.File;

public class SvnFormatSelector {

  @NotNull
  public static WorkingCopyFormat findRootAndGetFormat(final File path) {
    File root = SvnUtil.getWorkingCopyRootNew(path);

    return root != null ? getWorkingCopyFormat(root) : WorkingCopyFormat.UNKNOWN;
  }

  @NotNull
  public static WorkingCopyFormat getWorkingCopyFormat(final File path) {
    WorkingCopyFormat format = SvnUtil.getFormat(path);

    return WorkingCopyFormat.UNKNOWN.equals(format) ? SvnKitManager.getWorkingCopyFormat(path) : format;
  }
}
