// Copyright 2000-2018 JetBrains s.r.o.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.angularjs.refactoring;

import com.intellij.lang.ecmascript6.resolve.ES6PsiUtil;
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner;
import com.intellij.lang.javascript.refactoring.JSRenameExtension;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Angular2RenameExtension implements JSRenameExtension {
  private static final String[] CANDIDATE_EXTENSIONS = new String[]{"css", "scss", "less", "styl", "html"};

  @NotNull
  @Override
  public Map<PsiFile, String> getAdditionalFilesToRename(@NotNull PsiElement original,
                                                         @NotNull PsiFile originalFile,
                                                         @NotNull String newFileName) {
    if (!(original instanceof JSAttributeListOwner) ||
        ES6PsiUtil.findDecoratorByName((JSAttributeListOwner)original, "Component") == null) {
      return Collections.emptyMap();
    }
    VirtualFile componentVFile = originalFile.getVirtualFile();
    Map<PsiFile, String> result = new HashMap<>();
    PsiDirectory dir = originalFile.getParent();
    if (dir != null && dir.isDirectory()) {
      for (String extension : CANDIDATE_EXTENSIONS) {
        PsiFile file = dir.findFile(componentVFile.getNameWithoutExtension() + "." + extension);
        if (file != null) {
          result.put(file, newFileName + "." + extension);
        }
      }
    }
    return result;
  }
}
