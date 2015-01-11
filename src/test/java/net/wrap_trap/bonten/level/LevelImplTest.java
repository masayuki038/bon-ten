package net.wrap_trap.bonten.level;

import java.io.IOException;

import net.wrap_trap.bonten.Bonten;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class LevelImplTest {

  @Test
  public void testGetPath() throws IOException {
    Bonten.init();
    LevelImpl levelImpl = new LevelImpl("/foo/bar", 1, null);
    Assert.assertThat("/foo/bar/A-1.data", is(levelImpl.getFile("A").getAbsolutePath()));
  }
  
}
