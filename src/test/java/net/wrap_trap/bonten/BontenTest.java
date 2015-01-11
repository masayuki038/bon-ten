package net.wrap_trap.bonten;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class BontenTest {

  @Test
  public void testGetLevelsMin() throws IOException {
    String[] dataFiles = {
      "/data/file-1.data",
      "/data/file-2.data",
      "/data/file-3.data",
      "/data/file.data"
    };
    
    Bonten.init();
    Bonten bonten = new Bonten(null);
    Tuple<Integer, Integer> levels = bonten.getLevels(dataFiles);
    assertThat(levels.e1, is(10));
    assertThat(levels.e2, is(1));
  }
  
  @Test
  public void testGetLevelsMax() throws IOException {
    String[] dataFiles = {
      "/data/file-14.data",
      "/data/file-13.data",
      "/data/file-12.data",
      "/data/file-11.data",
      "/data/file.data"
    };
    
    Bonten.init();
    Bonten bonten = new Bonten(null);
    Tuple<Integer, Integer> levels = bonten.getLevels(dataFiles);
    assertThat(levels.e1, is(14));
    assertThat(levels.e2, is(10));
  }

  @Test
  public void testGetLevelsWithNoValidDataFiles() throws IOException {
    String[] dataFiles = {"/data/file.data"};
    Bonten.init();
    Bonten bonten = new Bonten(null);
    Tuple<Integer, Integer> levels = bonten.getLevels(dataFiles);
    assertThat(levels.e1, is(10));
    assertThat(levels.e2, is(10));
  }
  
  @Test
  public void testGetLevelsWithNoDataFiles() throws IOException {
    String[] dataFiles = {};
    Bonten.init();
    Bonten bonten = new Bonten(null);
    Tuple<Integer, Integer> levels = bonten.getLevels(dataFiles);
    assertThat(levels.e1, is(10));
    assertThat(levels.e2, is(10));
  }
  
  @Test
  public void test() {
    IntStream.range(1, 10).skip(2).forEach((e) -> System.out.println(e));
  }
  
}
