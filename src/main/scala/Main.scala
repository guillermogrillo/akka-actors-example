import akka.actor.typed.{ActorSystem, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors


object TextInterpreter extends App {
  val rootActor = ActorSystem(Main(), "root")
  rootActor ! "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
}

object Main {
  def apply(): Behavior[String] = Behaviors.setup(context => new Main(context))
}

class Main(context: ActorContext[String]) extends AbstractBehavior[String](context){
  override def onMessage(msg: String): Behavior[String] = {
    val wordCounterActor = context.spawn(WordCounterActor(), "word-counter-actor")
    println(s"WordCounterActor $wordCounterActor")
    wordCounterActor ! msg
    val charCounterActor = context.spawn(CharCounterActor(), "char-counter-actor")
    println(s"CharCounterActor $charCounterActor")
    charCounterActor ! msg
    this
  }
}

object WordCounterActor {
  def apply(): Behavior[String] = Behaviors.setup(context => new WordCounterActor(context))
}

class WordCounterActor(context: ActorContext[String]) extends AbstractBehavior[String](context){

  override def onMessage(msg: String): Behavior[String] = {
    val count = msg.split(" ").size
    println(s"I've counted ${count} words")
    Behaviors.stopped
    Behaviors.empty[String]
  }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("Stopping WordCounterActor")
      this
  }
}

object CharCounterActor {
  def apply(): Behavior[String] = Behaviors.setup(context => new CharCounterActor(context))
}

class CharCounterActor(context: ActorContext[String]) extends AbstractBehavior[String](context) {

  override def onMessage(msg: String): Behavior[String] = {
    val count = msg.toCharArray.size;
    println(s"I've counted ${count} chars")
    Behaviors.stopped
    Behaviors.empty[String]
  }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("Stopping CharCounterActor")
      this
  }
}