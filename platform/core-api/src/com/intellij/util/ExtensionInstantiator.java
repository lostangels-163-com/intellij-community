// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.util;

import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.PicoContainer;

public final class ExtensionInstantiator {
  private static final Logger LOG = Logger.getInstance(ExtensionInstantiator.class);

  private ExtensionInstantiator() {
  }

  @NotNull
  public static <T> T instantiateWithPicoContainerOnlyIfNeeded(@Nullable String className,
                                                               @NotNull PicoContainer picoContainer,
                                                               @Nullable PluginDescriptor pluginDescriptor) {
    PluginId pluginId = pluginDescriptor == null ? null : pluginDescriptor.getPluginId();
    if (className == null) {
      throw new PluginException("implementation class is not specified", pluginId);
    }

    Class<T> clazz;
    try {
      clazz = AbstractExtensionPointBean.findClass(className, pluginDescriptor);
    }
    catch (ClassNotFoundException e) {
      throw new PluginException(e, pluginId);
    }

    try {
      return ReflectionUtil.newInstance(clazz, false);
    }
    catch (Throwable e) {
      if (e.getCause() instanceof NoSuchMethodException) {
        LOG.error(new PluginException("Bean extension class constructor must not have parameters: " + className, pluginId));
      }
      else {
        throw new PluginException(e, pluginId);
      }
    }

    try {
      return AbstractExtensionPointBean.instantiate(clazz, picoContainer, true);
    }
    catch (Throwable e) {
      throw new PluginException(e, pluginId);
    }
  }
}
