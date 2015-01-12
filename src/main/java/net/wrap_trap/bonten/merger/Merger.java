package net.wrap_trap.bonten.merger;

import java.io.File;
import java.io.IOException;

import scala.concurrent.Future;
import net.wrap_trap.bonten.Utils;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;

public interface Merger {
  
  Future<Integer> start(File aFile, File bFile, File xFile, int bTreeSize, boolean hasNext) throws IOException;
  Future<Void> step(int wip);

  @SuppressWarnings("serial")
  static Merger createMerger() {
    Utils.ensureExpiry();
    ActorSystem actorSystem = ActorSystem.create("system");
    return TypedActor.get(actorSystem).typedActorOf(
      new TypedProps<MergerImpl>(Merger.class, new Creator<MergerImpl>() {
        public MergerImpl create() throws IOException { 
          return new MergerImpl(); 
        }
      }));
  }
}
