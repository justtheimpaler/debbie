package org.nocrala.tools.debbie.utils;

import java.io.File;

public class FUtil {

  public static File relativize(final File base, final File f) {
    return new File(base.toURI().relativize(f.toURI()).getPath());
  }

}
