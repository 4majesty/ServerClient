package vfs.test;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestSocketServer {
	
	public class TSSThread extends Thread{
		Socket socket = null;
		
		public TSSThread(Socket socket){
			this.socket = socket;
		}
		
		public void run(){
			try {
				InputStream in = socket.getInputStream();
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				
				int b = 0;
				int ends = 0;
				while(true){
					byte[] filename = new byte[256];
                    b = 0;
                    while(b<filename.length){
                        b += in.read(filename, b, filename.length-b);
                    }
                    ends = 0;
                    for(int i=0;i<filename.length;i++){
                        if(filename[i]=='\0'){
                            ends = i;
                            break;
                        }
                    }
                    String filenames = new String(filename,0,ends);
                    System.out.println("filenames: "+filenames);
                    File fileout = new File(filenames);
                    if(fileout.isFile()){
                        throw new Exception("file exists"+fileout.getAbsolutePath());
                    }
                    FileOutputStream fos = new FileOutputStream(fileout);
                    
                    byte[] filesize = new byte[64];
                    b = 0;
                    while(b<filesize.length){
                        b += in.read(filesize, b, filesize.length-b);
                    }
                    ends = 0;
                    for(int i=0;i<filesize.length;i++){
                        if(filesize[i]=='\0'){
                            ends = i;
                            break;
                        }
                    }
                    String filesizes = new String(filesize,0,ends);
                    System.out.println("filesize: "+filesizes);
                    int ta = Integer.parseInt(filesizes);
                    
                    byte[] buf = new byte[1024*10];
                    
                    while(true){
                    	if(ta == 0){
                    		break;
                    	}
                    	int len = ta;
                    	if(len>buf.length){
                            len = buf.length;
                        }
                    	int rlen = in.read(buf, 0, len);
                    	ta -= rlen;
                        if(rlen>0){
                            fos.write(buf,0,rlen);
                            fos.flush();
                        }
                        else{
                            break;
                        }
                    }
                    
                    System.out.println("file transformation finish!");
                    fos.close();
                    break;
				}
				
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public class ChunkServerTest extends Thread{
		private Socket socket = null;
		
		public ChunkServerTest(Socket socket){
			this.socket = socket;
			System.out.println("ChunkServerTest start..");
		}
		
		public void run(){
			System.out.println("ChunkServerTest run..");
			try {
				InputStream in = socket.getInputStream();
//				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());    
				
				int b = 0;
				int ends = 0;
				while(true){
					byte[] protocolBuf = new byte[8];
					
					while(b < protocolBuf.length){
                        b += in.read(protocolBuf, b, protocolBuf.length-b);
                    }
                    ends = 0;
                    for(int i = 0; i < protocolBuf.length; ++i){
                        if(protocolBuf[i]=='\0'){
                            ends = i;
                            break;
                        }
                    }
                    String protocolStr = new String(protocolBuf, 0, ends);
                    int protocol = Integer.parseInt(protocolStr);
                    System.out.println("protocol: " + protocol);
                    
                    if(protocol == 1000){
                    	writeChunk(in, out);
                    }
                    if(protocol == 1001){
                    	readChunk(in, out);
                    }
                    break;
				}
				
				in.close();
				out.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void writeChunk(InputStream in, DataOutputStream out) throws IOException{
			System.out.println("start to write chunk..");
        	
        	// chunk id
        	byte[] chunkBuf = new byte[64];
        	int b = 0;
        	while(b < chunkBuf.length){
        		b += in.read(chunkBuf, b, chunkBuf.length-b);
        	}
        	int ends = 0;
            for(int i = 0; i < chunkBuf.length; ++i){
                if(chunkBuf[i]=='\0'){
                    ends = i;
                    break;
                }
            }
            String chunkStr = new String(chunkBuf, 0, ends);
            int chunkId = Integer.parseInt(chunkStr);
            System.out.println("chunk id: "+ chunkId);
            
            // offset
            byte[] offsetBuf = new byte[64];
            b = 0;
            while(b < offsetBuf.length){
            	b += in.read(offsetBuf, b, offsetBuf.length-b);
            }
            ends = 0;
            for(int i = 0; i < offsetBuf.length; ++i){
            	if(offsetBuf[i] == '\0'){
            		ends = i;
            		break;
            	}
            }
            String offsetStr = new String(offsetBuf, 0, ends);
            int offset = Integer.parseInt(offsetStr);
            System.out.println("offset: "+ offset);
        	
            // writing len
            byte[] lenBuf = new byte[64];
            b = 0;
            while(b < lenBuf.length){
            	b += in.read(lenBuf, b, lenBuf.length-b);
            }
            ends = 0;
            for(int i = 0; i < lenBuf.length; ++i){
            	if(lenBuf[i] == '\0'){
            		ends = i;
            		break;
            	}
            }
            String lenStr = new String(lenBuf, 0, ends);
            int len = Integer.parseInt(lenStr);
            System.out.println("len: "+ len);
            
            System.out.println("chunk transformation finish!");
            out.writeUTF("OK");
		}
		
		private void readChunk(InputStream in, DataOutputStream out) throws IOException{
			System.out.println("start to read chunk..");
        	
        	// chunk id
        	byte[] chunkBuf = new byte[64];
            String chunkStr = readString(in, chunkBuf);
            int chunkId = Integer.parseInt(chunkStr);
            System.out.println("chunk id: "+ chunkId);
            //offset
            byte[] offsetBuf = new byte[64];
            String offsetStr = readString(in, offsetBuf);
            int offset = Integer.parseInt(offsetStr); 
            System.out.println("offset: "+ offset);
            //read len
            byte[] lenBuf = new byte[64];
            String lenStr = readString(in, lenBuf);
            int len = Integer.parseInt(lenStr); 
            System.out.println("len: "+ len);
            
            out.writeUTF("OK");
            
            int chunkSize = 64*1024;
            int fakeBufSize = 8*1024;
            
            out.writeInt(chunkSize);
            
            byte[] fakeBuf = new byte[fakeBufSize];
            int writeCount = 0;
            while(writeCount < chunkSize){
            	int writeNum = Math.min(chunkSize-writeCount, fakeBufSize);
            	out.write(fakeBuf, 0, writeNum);
            	out.flush();
            	writeCount += writeNum;
            }
		}
		
		private String readString(InputStream in, byte[] buf) throws IOException{
			int b = 0;
        	while(b < buf.length){
        		b += in.read(buf, b, buf.length-b);
        	}
        	int ends = 0;
            for(int i = 0; i < buf.length; ++i){
                if(buf[i]=='\0'){
                    ends = i;
                    break;
                }
            }
            return new String(buf, 0, ends);
		}
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 8877;
		TestSocketServer server = new TestSocketServer();
		
		try {
			ServerSocket ss = new ServerSocket(port);
			
			while(true){
	            Socket socket = ss.accept();
	            server.new ChunkServerTest(socket).start();
	        }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
