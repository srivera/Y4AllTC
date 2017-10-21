/*
 * This file is part of JSTUN. 
 * 
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 * 
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package ec.com.yacare.y4all.lib.stun;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.widget.TextView;

public class DiscoveryTest {
//	private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryTest.class);
	InetAddress iaddress;
	String stunServer;
	int port;
	int timeoutInitValue = 300; //ms
	MappedAddress ma = null;
	ChangedAddress ca = null;
	boolean nodeNatted = true;
	DatagramSocket socketTest1 = null;
	DiscoveryInfo di = null;
	
	TextView origen;
	
	public DiscoveryTest(InetAddress iaddress , String stunServer, int port, TextView origen) {
		super();
		this.iaddress = iaddress;
		this.stunServer = stunServer;
		this.port = port;
		this.origen = origen;
	}
		
	public DiscoveryInfo test() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageAttributeException, MessageHeaderParsingException{
		ma = null;
		ca = null;
		nodeNatted = true;
		socketTest1 = null;
		di = new DiscoveryInfo(iaddress);
		
		if (test1()) {
			if (test2()) {
				if (test1Redo()) {
					test3();
				}
			}
		}
		
		socketTest1.close();
		
		return di;
	}
	
	private boolean test1() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageHeaderParsingException {
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			try {
				// Test 1 including response
				socketTest1 = new DatagramSocket(new InetSocketAddress(iaddress, 0));
				socketTest1.setReuseAddress(true);
				socketTest1.connect(InetAddress.getByName(stunServer), port);
				socketTest1.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				sendMH.addMessageAttribute(changeRequest);
				
				byte[] data = sendMH.getBytes();
				DatagramPacket send = new DatagramPacket(data, data.length);
				socketTest1.send(send);
				origen.post(new Runnable() {
					
					@Override
					public void run() {
						origen.setText("Test 1: Binding Request sent.");
						
					}
				});
				System.out.println("Test 1: Binding Request sent.");
			
				MessageHeader receiveMH = new MessageHeader();
				while (!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					socketTest1.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
					receiveMH.parseAttributes(receive.getData());
				}
				
				ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
				ca = (ChangedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ChangedAddress);
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Message header contains an Errorcode message attribute..");
							
						}
					});
					System.out.println("Message header contains an Errorcode message attribute.");
					return false;
				}
				if ((ma == null) || (ca == null)) {
					di.setError(700, "The server is sending an incomplete response (Mapped Address and Changed Address message attributes are missing). The client should not retry.");
					
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Response does not contain a Mapped Address or Changed Address message attribute.");
							
						}
					});
					System.out.println("Response does not contain a Mapped Address or Changed Address message attribute.");
					return false;
				} else {
					di.setPublicIP(ma.getAddress().getInetAddress());
					if ((ma.getPort() == socketTest1.getLocalPort()) && (ma.getAddress().getInetAddress().equals(socketTest1.getLocalAddress()))) {
						System.out.println("Node is not natted.");
						origen.post(new Runnable() {
							
							@Override
							public void run() {
								origen.setText("Node is not natted.");
								
							}
						});
						nodeNatted = false;
					} else {
						origen.post(new Runnable() {
							
							@Override
							public void run() {
								origen.setText("Node is natted.");
								
							}
						});
						System.out.println("Node is natted.");
					}
					return true;
				}
			} catch (SocketTimeoutException ste) {
				if (timeSinceFirstTransmission < 7900) {
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 1: Socket timeout while receiving the response.");
							
						}
					});
					System.out.println("Test 1: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
					// node is not capable of udp communication
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 1: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
							
						}
					});
					System.out.println("Test 1: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
					di.setBlockedUDP();
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Node is not capable of UDP communication.");
							
						}
					});
					System.out.println("Node is not capable of UDP communication.");
					return false;
				}
			} 
		}
	}
		
	private boolean test2() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageAttributeException, MessageHeaderParsingException {
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			try {
				// Test 2 including response
				DatagramSocket sendSocket = new DatagramSocket(new InetSocketAddress(iaddress, 0));
				sendSocket.connect(InetAddress.getByName(stunServer), port);
				sendSocket.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				changeRequest.setChangeIP();
				changeRequest.setChangePort();
				sendMH.addMessageAttribute(changeRequest);
					 
				byte[] data = sendMH.getBytes(); 
				DatagramPacket send = new DatagramPacket(data, data.length);
				sendSocket.send(send);
				origen.post(new Runnable() {
					
					@Override
					public void run() {
						origen.setText("Test 2: Binding Request sent.");
						
					}
				});
				System.out.println("Test 2: Binding Request sent.");
				
				int localPort = sendSocket.getLocalPort();
				InetAddress localAddress = sendSocket.getLocalAddress();
				
				sendSocket.close();
				
				DatagramSocket receiveSocket = new DatagramSocket(localPort, localAddress);
				receiveSocket.connect(ca.getAddress().getInetAddress(), ca.getPort());
				receiveSocket.setSoTimeout(timeout);
				
				MessageHeader receiveMH = new MessageHeader();
				while(!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					receiveSocket.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
					receiveMH.parseAttributes(receive.getData());
				}
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Message header contains an Errorcode message attribute.");
							
						}
					});
					System.out.println("Message header contains an Errorcode message attribute.");
					return false;
				}
				if (!nodeNatted) {
					di.setOpenAccess();
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Node has open access to the Internet (or, at least the node is behind a full-cone NAT without translation).");
							
						}
					});
					System.out.println("Node has open access to the Internet (or, at least the node is behind a full-cone NAT without translation).");
				} else {
					di.setFullCone();
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Node is behind a full-cone NAT.");
							
						}
					});
					System.out.println("Node is behind a full-cone NAT.");
				}
				return false;
			} catch (SocketTimeoutException ste) {
				if (timeSinceFirstTransmission < 7900) {
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 2: Socket timeout while receiving the response.");
							
						}
					});
					System.out.println("Test 2: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 2: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
							
						}
					});
					System.out.println("Test 2: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
					if (!nodeNatted) {
						di.setSymmetricUDPFirewall();
						origen.post(new Runnable() {
							
							@Override
							public void run() {
								origen.setText("Node is behind a symmetric UDP firewall.");
								
							}
						});
						System.out.println("Node is behind a symmetric UDP firewall.");
						return false;
					} else {
						// not is natted
						// redo test 1 with address and port as offered in the changed-address message attribute
						return true;
					}
				}
			}
		}
	}
	
	private boolean test1Redo() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageHeaderParsingException{
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			// redo test 1 with address and port as offered in the changed-address message attribute
			try {
				// Test 1 with changed port and address values
				socketTest1.connect(ca.getAddress().getInetAddress(), ca.getPort());
				socketTest1.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				sendMH.addMessageAttribute(changeRequest);
				
				byte[] data = sendMH.getBytes();
				DatagramPacket send = new DatagramPacket(data, data.length);
				socketTest1.send(send);
				origen.post(new Runnable() {
					
					@Override
					public void run() {
						origen.setText("Test 1 redo with changed address: Binding Request sent.");
						
					}
				});
				System.out.println("Test 1 redo with changed address: Binding Request sent.");
				
				MessageHeader receiveMH = new MessageHeader();
				while (!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					socketTest1.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
					receiveMH.parseAttributes(receive.getData());
				}
				MappedAddress ma2 = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Message header contains an Errorcode message attribute.");
							
						}
					});
					System.out.println("Message header contains an Errorcode message attribute.");
					return false;
				}
				if (ma2 == null) {
					di.setError(700, "The server is sending an incomplete response (Mapped Address message attribute is missing). The client should not retry.");
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Response does not contain a Mapped Address message attribute.");
							
						}
					});
					System.out.println("Response does not contain a Mapped Address message attribute.");
					return false;
				} else {
					if ((ma.getPort() != ma2.getPort()) || (!(ma.getAddress().getInetAddress().equals(ma2.getAddress().getInetAddress())))) {
						di.setSymmetric();
						origen.post(new Runnable() {
							
							@Override
							public void run() {
								origen.setText("Node is behind a symmetric NAT.");
								
							}
						});
						System.out.println("Node is behind a symmetric NAT.");
						return false;
					}
				}
				return true;
			} catch (SocketTimeoutException ste2) {
				if (timeSinceFirstTransmission < 7900) {
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 1 redo with changed address: Socket timeout while receiving the response.");
							
						}
					});
					System.out.println("Test 1 redo with changed address: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 1 redo with changed address: Socket timeout while receiving the response.  Maximum retry limit exceed. Give up.");
							
						}
					});
					System.out.println("Test 1 redo with changed address: Socket timeout while receiving the response.  Maximum retry limit exceed. Give up.");
					return false;
				}
			}
		}
	}
	
	private void test3() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageAttributeException, MessageHeaderParsingException {
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			try {
				// Test 3 including response
				DatagramSocket sendSocket = new DatagramSocket(new InetSocketAddress(iaddress, 0));
				sendSocket.connect(InetAddress.getByName(stunServer), port);
				sendSocket.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				changeRequest.setChangePort();
				sendMH.addMessageAttribute(changeRequest);
				
				byte[] data = sendMH.getBytes();
				DatagramPacket send = new DatagramPacket(data, data.length);
				sendSocket.send(send);
				origen.post(new Runnable() {
					
					@Override
					public void run() {
						origen.setText("Test 3: Binding Request sent.");
						
					}
				});
				System.out.println("Test 3: Binding Request sent.");
				
				int localPort = sendSocket.getLocalPort();
				InetAddress localAddress = sendSocket.getLocalAddress();
				
				sendSocket.close();
				
				DatagramSocket receiveSocket = new DatagramSocket(localPort, localAddress);
				receiveSocket.connect(InetAddress.getByName(stunServer), ca.getPort());
				receiveSocket.setSoTimeout(timeout);
				
				MessageHeader receiveMH = new MessageHeader();
				while (!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					receiveSocket.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
					receiveMH.parseAttributes(receive.getData());
				}
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Message header contains an Errorcode message attribute.");
							
						}
					});
					System.out.println("Message header contains an Errorcode message attribute.");
					return;
				}
				if (nodeNatted) {
					di.setRestrictedCone();
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Node is behind a port restricted NAT.");
							
						}
					});
					System.out.println("Node is behind a restricted NAT.");
					return;
				}
			} catch (SocketTimeoutException ste) {
				if (timeSinceFirstTransmission < 7900) {
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 3: Socket timeout while receiving the response.");
							
						}
					});
					System.out.println("Test 3: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
						origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Test 3: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
							
						}
					});
					System.out.println("Test 3: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
					di.setPortRestrictedCone();
					origen.post(new Runnable() {
						
						@Override
						public void run() {
							origen.setText("Node is behind a port restricted NAT.");
							
						}
					});
					System.out.println("Node is behind a port restricted NAT.");
					return;
				}
			}
		}
	}
}
