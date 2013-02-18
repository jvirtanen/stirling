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
package stirling.fix.session

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import stirling.fix.messages.fix42._
import stirling.test.{Conductor, Event}
import stirling.test.fix.{Settings, TestInitiator}

class LogonSpec0 extends WordSpec with MustMatchers {
  "FIX session" should {
    "send Logon message (Ref ID 1B: b.)" in {
      val acceptor  = newAcceptor
      val initiator = newInitiator

      initiator.expect(Event.Connected)
      acceptor.expect(Event.Connected)

      initiator.logon()
      acceptor.expectMsgType(MsgTypeValue.LOGON)

      acceptor.logon()
      initiator.expectMsgType(MsgTypeValue.LOGON)

      Conductor.conduct(Seq(acceptor, initiator))
    }
  }

  def settings     = Settings()
  def newAcceptor  = new SessionlessTestAcceptor(6666, settings)
  def newInitiator = new TestInitiator("localhost", 6666, settings)
}
