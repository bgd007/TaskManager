package ua.chernov.taskmanager.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class PacketTransporterObject implements PacketTransporter {
	private ObjectInputStream input;
	private ObjectOutputStream output;
	

	 PacketTransporterObject(Socket socket) throws IOException {
		 //input = createInputStream(socket);		 
		 //output = createOutputStream(socket);
		 output = new ObjectOutputStream(socket.getOutputStream());
		 input = new ObjectInputStream(socket.getInputStream());
	 }

	 
	 
	@Override
	public void send(Serializable obj) throws IOException {
		output.reset();
		output.writeObject(obj);
		output.flush();
	}

	@Override
	public Object receive() throws ClassNotFoundException, IOException  {
					Object inObject = input.readObject();
					return inObject;
	}


	@Override
	public InputStream getInputStream() {
		return (InputStream)input;
	}

	@Override
	public OutputStream getOutputStream() {
		return (OutputStream)output;
	}



	@Override
	public void closeOutput() throws IOException {
		output.close();		
	}



	@Override
	public void closeInput() throws IOException {
		input.close();		
	}
	

}
