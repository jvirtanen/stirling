/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stirling.test.fix

import org.scalatest.Assertions
import silvertip.{Connection, MessageParser}
import stirling.test.{Act, React, TestClient}
import stirling.fix.messages.{DefaultMessageVisitor, FixMessage, FixMessageParser, Message}
import stirling.fix.session.Session

class TestInitiator(
  hostname: String,
  port:     Int,
  settings: Settings
) extends TestClient(new FixMessageParser, hostname, port) {
  import TestInitiator._

  private val session = new Session(
    Settings.heartBtInt(settings),
    Settings.config(settings, Side.Initiator),
    settings.sessionStore,
    settings.messageFactory,
    settings.messageComparator
  )

  override protected def onMessage(connection: Connection[FixMessage], message: FixMessage) {
    session.receive(connection, message, new DefaultMessageVisitor {
      override def defaultAction(message: Message) {
        receive(Incoming(message))
      }
    })
  }

  override protected def keepAlive(connection: Connection[FixMessage]) {
    session.keepAlive(connection)
  }

  def expectMsgType(expectedMsgType: String) {
    import Assertions._

    enqueue(React(
      "expectMsgType(%s)".format(expectedMsgType),
      { received: Any =>
        received match {
          case Incoming(received) =>
            assert(received.getMsgType === expectedMsgType)
          case _ =>
            fail("")
        }
      }
    ))
  }

  def logon() {
    enqueue(Act(
      "logon",
      { connection: Connection[FixMessage] =>
        session.logon(connection)
      }
    ))
  }

  def send(message: Message) {
    enqueue(Act(
      "send(%s)".format(message),
      { connection: Connection[FixMessage] =>
        session.send(connection, message)
      }
    ))
  }
}

object TestInitiator {
  private case class Incoming(message: Message)
}
