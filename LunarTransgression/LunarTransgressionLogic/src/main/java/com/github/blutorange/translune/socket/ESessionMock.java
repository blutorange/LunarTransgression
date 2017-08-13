package com.github.blutorange.translune.socket;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.MessageHandler.Partial;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.eclipse.jdt.annotation.Nullable;

enum ESessionMock implements Session {
	INSTANCE;

	@Override
	public WebSocketContainer getContainer() {
		throw new RuntimeException("mock session");
	}

	@Override
	public void addMessageHandler(@Nullable final MessageHandler handler) throws IllegalStateException {
		throw new RuntimeException("mock session");
	}

	@Override
	public <T> void addMessageHandler(final @Nullable Class<T> clazz, @Nullable final Whole<T> handler) {
		throw new RuntimeException("mock session");
	}

	@Override
	public <T> void addMessageHandler(@Nullable final Class<T> clazz, @Nullable final Partial<T> handler) {

		throw new RuntimeException("mock session");
	}

	@Override
	public Set<@Nullable MessageHandler> getMessageHandlers() {

		throw new RuntimeException("mock session");
	}

	@Override
	public void removeMessageHandler(final @Nullable MessageHandler handler) {

		throw new RuntimeException("mock session");
	}

	@Override
	public String getProtocolVersion() {

		throw new RuntimeException("mock session");
	}

	@Override
	public String getNegotiatedSubprotocol() {
		throw new RuntimeException("mock session");
	}

	@Override
	public List<@Nullable Extension> getNegotiatedExtensions() {
		throw new RuntimeException("mock session");
	}

	@Override
	public boolean isSecure() {
		throw new RuntimeException("mock session");
	}

	@Override
	public boolean isOpen() {
		throw new RuntimeException("mock session");
	}

	@Override
	public long getMaxIdleTimeout() {
		throw new RuntimeException("mock session");
	}

	@Override
	public void setMaxIdleTimeout(final long milliseconds) {
		throw new RuntimeException("mock session");
	}

	@Override
	public void setMaxBinaryMessageBufferSize(final int length) {
		throw new RuntimeException("mock session");
	}

	@Override
	public int getMaxBinaryMessageBufferSize() {
		throw new RuntimeException("mock session");
	}

	@Override
	public void setMaxTextMessageBufferSize(final int length) {
		throw new RuntimeException("mock session");
	}

	@Override
	public int getMaxTextMessageBufferSize() {
		throw new RuntimeException("mock session");
	}

	@Override
	public Async getAsyncRemote() {
		throw new RuntimeException("mock session");
	}

	@Override
	public Basic getBasicRemote() {
		throw new RuntimeException("mock session");
	}

	@Override
	public String getId() {
		throw new RuntimeException("mock session");
	}

	@Override
	public void close() throws IOException {
		throw new RuntimeException("mock session");
	}

	@Override
	public void close(final @Nullable CloseReason closeReason) throws IOException {
		throw new RuntimeException("mock session");
	}

	@Override
	public URI getRequestURI() {
		throw new RuntimeException("mock session");
	}

	@Override
	public Map<String, List<String>> getRequestParameterMap() {
		throw new RuntimeException("mock session");
	}

	@Override
	public String getQueryString() {
		throw new RuntimeException("mock session");
	}

	@Override
	public Map<String, String> getPathParameters() {
		throw new RuntimeException("mock session");
	}

	@Override
	public Map<String, Object> getUserProperties() {
		throw new RuntimeException("mock session");
	}

	@Override
	public Principal getUserPrincipal() {
		throw new RuntimeException("mock session");
	}

	@Override
	public Set<Session> getOpenSessions() {
		throw new RuntimeException("mock session");
	}

}
