package net.wrap_trap.bonten;

import org.junit.Test;

import com.typesafe.config.ConfigException;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.core.Is.is;


public class BontenConfigTest {

	@Test
	public void testLoadConfig() {
		BontenConfig bontenConfig = BontenConfig.loadConfig();
		assertThat(bontenConfig.getInt("expiry_secs"), is(0));
	}
	
	@Test
	public void unregisteredKey() {
		try {
			BontenConfig bontenConfig = BontenConfig.loadConfig();
			assertThat(bontenConfig.getInt("expiry_sec"), is(0));
			fail();
		} catch(ConfigException ex) {}
	}
}
