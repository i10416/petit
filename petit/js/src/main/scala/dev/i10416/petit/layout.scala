package dev.i10416.petit

import org.scalajs.dom._
import cats.effect._
import scala.scalajs.js.annotation._
import fs2.Stream
import cats.effect.std.Queue
import cats.effect.std.Dispatcher
case class Layout(isDrawerOpen: Boolean = false)
case class State(layout: Layout = Layout())

@JSExportTopLevel("App")
object App {
  import cats.effect.unsafe.implicits.global
  private val queue = Queue.unbounded[IO, String]
  private val stateRef = Ref.of[IO, State](State())
  private def syncDrawer(
      q: Queue[IO, String],
      state: Ref[IO, State],
      elementId: String = "drawer"
  ): Stream[IO, Unit] = for {
    s <- Stream
      .fromQueueUnterminated(q)
      .evalMap {
        case event @ ("open" | "close") =>
          val doc = document.getElementById(elementId)
          for {
            _ <- IO.whenA(event == "close")(
              IO(doc.classList.remove("active")) *> state
                .update(s => s.copy(s.layout.copy(false)))
            )
            _ <- IO.whenA(event == "open")(
              IO(doc.classList.add("active")) *> state
                .update(s => s.copy(s.layout.copy(true)))
            )
          } yield ()
        case _ => IO.unit
      }
  } yield ()

  private def addClickEventListener(
      elementId: String,
      onClick: Event => IO[Unit],
      dispatcher: Dispatcher[IO]
  ): IO[Unit] = IO {
    val doc = document
      .getElementById(elementId)

    doc.addEventListener(
      "click",
      (event: Event) =>
        dispatcher.unsafeRunAndForget {
          onClick(event)
        }
    )
  }

  @JSExport
  def init(): Unit = {
    val program = Dispatcher.apply[IO].use { dispatcher =>
      for {
        q <- queue
        // restore state?
        stateRef <- Ref.of[IO, State](State())
        _ <- addClickEventListener(
          "drawer-button",
          (_) =>
            stateRef.get
              .map(_.layout.isDrawerOpen)
              .flatMap(isOpen =>
                if (isOpen) q.offer("close")
                else q.offer("open")
              ),
          dispatcher
        )
        _ <- syncDrawer(q, stateRef).compile.drain
        _ <- IO.println("terminated")
      } yield ()
    }
    program.unsafeRunAndForget()
  }
}
