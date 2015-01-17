package net.wrap_trap.bonten.level;

import java.io.IOException;

import net.wrap_trap.bonten.Bonten;

import org.junit.Assert;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import static org.hamcrest.core.Is.is;

public class LevelImplTest {

  @Test
  public void testGetPath() throws IOException {
    Bonten.init();
    final Props props = Props.create(Level.class, "/foo/bar", 1, null);
    final TestActorRef<Level> ref = TestActorRef.create(Bonten.actorSystem, props, "testA");
    final Level level = ref.underlyingActor();
    Assert.assertThat("/foo/bar/A-1.data", is(level.getFile("A").getAbsolutePath()));
  }
}
