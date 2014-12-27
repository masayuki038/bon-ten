package net.wrap_trap.bonten;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.typesafe.config.ConfigException;

public class BontenConfigTest {

  @Test
  public void testLoadConfig() {
    final BontenConfig bontenConfig = BontenConfig.loadConfig();
    assertThat(bontenConfig.getInt("expiry_secs"), is(0));
  }

  @Test
  public void unregisteredKey() {
    try {
      final BontenConfig bontenConfig = BontenConfig.loadConfig();
      assertThat(bontenConfig.getInt("expiry_sec"), is(0));
      fail();
    } catch (final ConfigException ex) {}
  }
}
