/*
 Copyright (C) 2001 - 2007 The Software Conservancy as Trustee. All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in the
 Software without restriction, including without limitation the rights to use, copy,
 modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so, subject to the
 following conditions:

 The above copyright notice and this permission notice shall be included in all 
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Nothing in this notice shall be deemed to grant any rights to trademarks, copyrights,
 patents, trade secrets or any other intellectual property of the licensor or any
 contributor except as expressly stated herein. No patent license is granted separate
 from the Software, for code that you delete from the Software, or for combinations
 of the Software with other software or hardware.
*/
package org.openadaptor.auxil.connector.jms;

import org.openadaptor.core.exception.RecordFormatException;

import javax.jms.*;
import java.io.Serializable;
/*
 * File: $Header: $
 * Rev:  $Revision: $
 * Created Apr 23, 2007 by oa3 Core Team
 */

/**
 * Default implementation of IMessageGenerator that generates either a TextMessage or an ObjectMessage
 * depending on the type of the messageRecord. If the messageRecord is neither a String nor an instance
 * of Serializable then a RecordFormatException is thrown.
 */
public class DefaultMessageGenerator implements IMessageGenerator {
  public Message createMessage(Object messageRecord, Session session) throws JMSException {
    Message msg;
    if (messageRecord instanceof String) {
      TextMessage textMsg = session.createTextMessage();
      textMsg.setText((String) messageRecord);
      msg = textMsg;
    } else if (messageRecord instanceof Serializable) {
      ObjectMessage objectMsg = session.createObjectMessage();
      objectMsg.setObject((Serializable) messageRecord);
      msg = objectMsg;
    } else {
      // We have not needed more message types in practice.
      // If we do need them in future this is where they go.
      throw new RecordFormatException("Undeliverable record type [" + messageRecord.getClass().getName() + "]");
    }
    return msg;
  }
}
