package net.wrap_trap.bonten.level;

import java.io.IOException;

import net.wrap_trap.bonten.Utils;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;

public interface Level {
  
  

  int unmergedCount();

  @SuppressWarnings("serial")
  static Level open(String dirPath, int level, Level next) {
    Utils.ensureExpiry();
    ActorSystem actorSystem = ActorSystem.create("system");
    return TypedActor.get(actorSystem).typedActorOf(
      new TypedProps<LevelImpl>(Level.class, new Creator<LevelImpl>() {
        public LevelImpl create() throws IOException { 
          return new LevelImpl(dirPath, level, next); 
        }
      }));
  }
}
