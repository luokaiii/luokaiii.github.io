```java
	/**
	 * Handle incoming WebSocket messages from clients.
	 */
	public void handleMessageFromClient(WebSocketSession session,
			WebSocketMessage<?> webSocketMessage, MessageChannel outputChannel) {

		List<Message<byte[]>> messages;
		try {
			ByteBuffer byteBuffer;
			if (webSocketMessage instanceof TextMessage) {
				byteBuffer = ByteBuffer.wrap(((TextMessage) webSocketMessage).asBytes());
			}
			else if (webSocketMessage instanceof BinaryMessage) {
				byteBuffer = ((BinaryMessage) webSocketMessage).getPayload();
			}
			else {
				return;
			}

			BufferingStompDecoder decoder = this.decoders.get(session.getId());
			if (decoder == null) {
				throw new IllegalStateException("No decoder for session id '" + session.getId() + "'");
			}

			messages = decoder.decode(byteBuffer);
			if (messages.isEmpty()) {
				if (logger.isTraceEnabled()) {
					logger.trace("Incomplete STOMP frame content received in session " +
							session + ", bufferSize=" + decoder.getBufferSize() +
							", bufferSizeLimit=" + decoder.getBufferSizeLimit() + ".");
				}
				return;
			}
		}
		catch (Throwable ex) {
			if (logger.isErrorEnabled()) {
				logger.error("Failed to parse " + webSocketMessage +
						" in session " + session.getId() + ". Sending STOMP ERROR to client.", ex);
			}
			handleError(session, ex, null);
			return;
		}

		for (Message<byte[]> message : messages) {
			try {
				StompHeaderAccessor headerAccessor =
						MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				Assert.state(headerAccessor != null, "No StompHeaderAccessor");

				headerAccessor.setSessionId(session.getId());
				headerAccessor.setSessionAttributes(session.getAttributes());
				headerAccessor.setUser(getUser(session));
				headerAccessor.setHeader(SimpMessageHeaderAccessor.HEART_BEAT_HEADER, headerAccessor.getHeartbeat());
				if (!detectImmutableMessageInterceptor(outputChannel)) {
					headerAccessor.setImmutable();
				}

				if (logger.isTraceEnabled()) {
					logger.trace("From client: " + headerAccessor.getShortLogMessage(message.getPayload()));
				}

				StompCommand command = headerAccessor.getCommand();
				boolean isConnect = StompCommand.CONNECT.equals(command) || StompCommand.STOMP.equals(command);
				if (isConnect) {
					this.stats.incrementConnectCount();
				}
				else if (StompCommand.DISCONNECT.equals(command)) {
					this.stats.incrementDisconnectCount();
				}

				try {
					SimpAttributesContextHolder.setAttributesFromMessage(message);
					boolean sent = outputChannel.send(message);

					if (sent) {
						if (isConnect) {
							Principal user = headerAccessor.getUser();
							if (user != null && user != session.getPrincipal()) {
								this.stompAuthentications.put(session.getId(), user);
							}
						}
						if (this.eventPublisher != null) {
							Principal user = getUser(session);
							if (isConnect) {
								publishEvent(this.eventPublisher, new SessionConnectEvent(this, message, user));
							}
							else if (StompCommand.SUBSCRIBE.equals(command)) {
								publishEvent(this.eventPublisher, new SessionSubscribeEvent(this, message, user));
							}
							else if (StompCommand.UNSUBSCRIBE.equals(command)) {
								publishEvent(this.eventPublisher, new SessionUnsubscribeEvent(this, message, user));
							}
						}
					}
				}
				finally {
					SimpAttributesContextHolder.resetAttributes();
				}
			}
			catch (Throwable ex) {
				if (logger.isErrorEnabled()) {
					logger.error("Failed to send client message to application via MessageChannel" +
							" in session " + session.getId() + ". Sending STOMP ERROR to client.", ex);
				}
				handleError(session, ex, message);
			}
		}
	}
```

