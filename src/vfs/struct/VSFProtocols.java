package vfs.struct;

public class VSFProtocols {
	public static final int WRITE_CHUNK = 1000;
	public static final int READ_CHUNK = 1001;

	
	public static final int INITIALIZE_CHUNK_INFO = 2000;
	public static final int NEW_CHUNK = 2001;
	public static final int RELEASE_CHUNK = 2002;
	public static final int HEART_BEAT_DETECT_TO_SLAVE = 2003;
	
	
	public static final int MK_DIR = 3001;
	public static final int CREATE_FILE = 3002;
	public static final int RESIZE_FILE = 3003;
	public static final int ADD_CHUNK = 3004;
	public static final int OPEN_FILE = 3005;
	public static final int REMOVE_FILE = 3006;
	public static final int GET_FILE_NODE = 3007;
	
	
	public static String MESSAGE_OK = "OK";

}
