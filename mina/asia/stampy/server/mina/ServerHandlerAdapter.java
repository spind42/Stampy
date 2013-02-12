/*
 * Copyright (C) 2013 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package asia.stampy.server.mina;

import java.lang.invoke.MethodHandles;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.stampy.client.message.AbstractClientMessageHeader;
import asia.stampy.client.message.ClientMessageHeader;
import asia.stampy.client.message.connect.ConnectHeader;
import asia.stampy.client.message.connect.ConnectMessage;
import asia.stampy.client.message.stomp.StompMessage;
import asia.stampy.common.HostPort;
import asia.stampy.common.message.StampyMessage;
import asia.stampy.common.message.StompMessageType;
import asia.stampy.common.message.interceptor.InterceptException;
import asia.stampy.server.message.connected.ConnectedMessage;
import asia.stampy.server.message.error.ErrorMessage;
import asia.stampy.server.message.receipt.ReceiptMessage;

/**
 * The Class ServerHandlerAdapter.
 */
class ServerHandlerAdapter {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private ServerMinaMessageGateway gateway;

  /**
   * Checks if is valid message.
   * 
   * @param message
   *          the message
   * @return true, if is valid message
   */
  boolean isValidMessage(StampyMessage<?> message) {
    switch (message.getMessageType()) {

    case ABORT:
    case ACK:
    case BEGIN:
    case COMMIT:
    case CONNECT:
    case STOMP:
    case DISCONNECT:
    case NACK:
    case SEND:
    case SUBSCRIBE:
    case UNSUBSCRIBE:
      message.validate();
      return true;
    default:
      return false;

    }
  }

  /**
   * Error handle.
   * 
   * @param message
   *          the message
   * @param e
   *          the e
   * @param hostPort
   *          the host port
   * @throws InterceptException
   *           the intercept exception
   */
  void errorHandle(StampyMessage<?> message, Exception e, HostPort hostPort) throws InterceptException {
    log.error("Handling error, sending error message to " + hostPort, e);
    String receipt = message.getHeader().getHeaderValue(AbstractClientMessageHeader.RECEIPT);

    ErrorMessage error = new ErrorMessage(StringUtils.isEmpty(receipt) ? "n/a" : receipt);
    error.getHeader().setMessageHeader("Could not execute " + message.getMessageType() + " - " + e.getMessage());

    getMessageGateway().sendMessage(error, hostPort);
  }

  /**
   * Send response if required.
   * 
   * @param message
   *          the message
   * @param session
   *          the session
   * @param hostPort
   *          the host port
   * @throws InterceptException
   *           the intercept exception
   */
  void sendResponseIfRequired(StampyMessage<?> message, IoSession session, HostPort hostPort) throws InterceptException {
    if (isConnectMessage(message)) {
      sendConnected(((ConnectMessage) message).getHeader(), session, hostPort);
      log.debug("Sent CONNECTED message to {}", hostPort);
      return;
    }

    if (isStompMessage(message)) {
      sendConnected(((StompMessage) message).getHeader(), session, hostPort);
      log.debug("Sent CONNECTED message to {}", hostPort);
      return;
    }

    String receipt = ((ClientMessageHeader) message.getHeader()).getReceipt();
    if (StringUtils.isEmpty(receipt)) return;

    ReceiptMessage msg = new ReceiptMessage(receipt);

    getMessageGateway().sendMessage(msg, hostPort);
    log.debug("Sent RECEIPT message to {}", hostPort);
  }

  private void sendConnected(ConnectHeader header, IoSession session, HostPort hostPort) throws InterceptException {
    ConnectedMessage message = new ConnectedMessage("1.2");

    int requested = message.getHeader().getIncomingHeartbeat();
    if (requested >= 0 || gateway.getHeartbeat() >= 0) {
      int heartbeat = Math.max(requested, gateway.getHeartbeat());
      message.getHeader().setHeartbeat(heartbeat, header.getOutgoingHeartbeat());
      message.getHeader().setSession(Long.toString(session.getId()));
    }

    getMessageGateway().sendMessage(message, hostPort);
  }

  private boolean isConnectMessage(StampyMessage<?> message) {
    return message.getMessageType().equals(StompMessageType.CONNECT);
  }

  private boolean isStompMessage(StampyMessage<?> message) {
    return message.getMessageType().equals(StompMessageType.STOMP);
  }

  /**
   * Gets the message gateway.
   * 
   * @return the message gateway
   */
  public ServerMinaMessageGateway getMessageGateway() {
    return gateway;
  }

  /**
   * Sets the message gateway.
   * 
   * @param gateway
   *          the new message gateway
   */
  public void setGateway(ServerMinaMessageGateway gateway) {
    this.gateway = gateway;
  }

}