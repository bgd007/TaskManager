﻿package ua.chernov.taskmanager.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public interface PacketTransporter {
	void send(Serializable obj) throws IOException;

	Object receive() throws ClassNotFoundException, IOException;

	InputStream getInputStream();
	OutputStream getOutputStream();
	
	void closeOutput() throws IOException;
	void closeInput() throws IOException;
}
