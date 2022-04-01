package manualtest;

import java.io.File;

import org.nocrala.tools.database.db.utils.FUtil;

public class RelPaths {

  public static void main(final String[] args) {
    File basedir = new File("./target");
    File f = new File("target/dir1/dir2/file2.txt");

    File f2 = FUtil.relativize(basedir, f);
    System.out.println("f2=" + f2);
  }

}
