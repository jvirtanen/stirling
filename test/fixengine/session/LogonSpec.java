/*
 * Copyright 2010 the original author or authors.
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
package fixengine.session;

import jdave.junit4.JDaveRunner;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import fixengine.messages.EncryptMethodValue;
import fixengine.messages.MsgTypeValue;

import fixengine.tags.EncryptMethod;
import fixengine.tags.HeartBtInt;

@RunWith(JDaveRunner.class) public class LogonSpec extends InitiatorSpecification {
    public class InitializedSession {
        /* Ref ID 1B: b. Send Logon message */
        public void valid() throws Exception {
            server.expect(MsgTypeValue.LOGON);
            server.respondLogon();
            runInClient(new Runnable() {
                @Override public void run() {
                    session.logon(connection);
                }
            });
        }

        /* Ref ID 1B: c. Valid Logon message as response is received */
        public void validButMsgSeqNumIsTooHigh() throws Exception {
            server.expect(MsgTypeValue.LOGON);
            server.respond(
                    new MessageBuilder(MsgTypeValue.LOGON)
                        .msgSeqNum(2)
                        .integer(HeartBtInt.TAG, getHeartbeatIntervalInSeconds())
                        .enumeration(EncryptMethod.TAG, EncryptMethodValue.NONE)
                    .build());
            server.expect(MsgTypeValue.RESEND_REQUEST);
            runInClient(new Runnable() {
                @Override public void run() {
                    session.logon(connection);
                }
            });
        }

        /* Ref ID 1B: d. Invalid Logon message is received */
        public void invalid() throws Exception {
            // TODO: Invalid MsgType
            // TODO: Garbled message
            server.expect(MsgTypeValue.LOGON);
            server.respond(
                    new MessageBuilder(MsgTypeValue.LOGON)
                        .msgSeqNum(1)
                        .integer(HeartBtInt.TAG, getHeartbeatIntervalInSeconds())
                        /* EncryptMethod(98) missing */
                    .build());
            server.expect(MsgTypeValue.LOGOUT);
            checking(new Expectations() {{
                one(logger).severe("EncryptMethod(98): Tag missing");
            }});
            runInClient(new Runnable() {
                @Override public void run() {
                    session.logon(connection);
                }
            });
        }

        /* Ref ID 1B: e. Receive any message other than a Logon message. */
        public void otherMessageThanLogon() throws Exception {
            server.expect(MsgTypeValue.LOGON);
            server.respond(
                    new MessageBuilder(MsgTypeValue.HEARTBEAT)
                        .msgSeqNum(1)
                    .build());
            server.expect(MsgTypeValue.LOGOUT);
            checking(new Expectations() {{
                one(logger).severe("first message is not a logon");
            }});
            runInClient(new Runnable() {
                @Override public void run() {
                    session.logon(connection);
                }
            });
        }
    }
}