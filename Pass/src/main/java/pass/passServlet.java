package pass;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.pqc.jcajce.provider.mceliece.McElieceKeysToParams;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;
import org.bouncycastle.util.encoders.Base64;

import Room.Room;
import Room.RoomDAO;
import User.user;
import User.userDAO;
import serial.serialDAO;

/**
 * Servlet implementation class passServlet
 */
@WebServlet("/pass")
public class passServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public passServlet() {
        super();
        
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if( action == null) {
			action = "list";
		}
		userDAO UD = new userDAO();
		serialDAO SD = new serialDAO();
		RoomDAO RD = new RoomDAO();
		Security.addProvider(new BouncyCastlePQCProvider());
		
		System.out.println("action = "+action);
		
		if(action.equals("newbi")) {
			String id = request.getParameter("id");
			String name = request.getParameter("name");
			System.out.println("name: " + name);
			serialDAO serial = new serialDAO();
			try {
				if(serial.check(id)) {
					serial.deleteSerial(id);
					user u = new user();
					u.setName(name);
					u.setDeviceID(null);
					UD.addUser(u);
					String key = createKey(name);
					response.getWriter().write("requestDeviceID,"+key);
				} else {
					response.getWriter().write("NotEnroll");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(action.equals("responseDeviceID")) {
			String DeviceID = request.getParameter("DeviceID");
			String name = request.getParameter("name");
			try {
				PrivateKey prikey = getPrivateKey(name+".pem");
				DeviceID = decrypt(DeviceID, prikey);
				UD.update(name, DeviceID);
				response.getWriter().write("Enroll");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} else if(action.equals("scan")){
			String encryted = request.getParameter("encryted");
			String[] buffer = encryted.split(",");
			String name = buffer[0];
			encryted = buffer[1];
			System.out.println("name: "+name);
			try {
				PrivateKey prikey = getPrivateKey(name+".pem");
				encryted = decrypt(encryted, prikey);
				if(UD.check(encryted)) {
					response.getWriter().write("Ok");
					Room r = new Room();
					r.setDeviceID(encryted);
					RD.insert(r);
				}else {
					response.getWriter().write("No");
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else if(action.equals("list")){
				try {
					request.setAttribute("serialTable", SD.getAll());
					request.setAttribute("userTable", UD.getAll());
					request.setAttribute("roomTable", RD.getAll());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				getServletContext().getRequestDispatcher("/Main.jsp").forward(request, response);
		}
	}
	
	
	public String createKey(String name) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException {
		
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("McEliece", "BCPQC");
		
		McElieceKeyGenParameterSpec params = new McElieceKeyGenParameterSpec(9, 33);
        kpg.initialize(params);
        	
        KeyPair mcelieceKp = kpg.generateKeyPair();
        Key publicKey = mcelieceKp.getPublic();
        Key privateKey = mcelieceKp.getPrivate();
        
        writePemFile(privateKey, name+".pem");
        
        String encodedPublic = Base64.toBase64String(publicKey.getEncoded());
        
        return encodedPublic;
	}

	private void writePemFile(Key key, String name) throws IOException {
    Pem pemFile = new Pem(key, "privateKey");
    pemFile.write(name);
	}
	
	public PrivateKey getPrivateKey(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        String temp = new String(keyBytes);
        String publicKeyPEM = temp.replace("-----BEGIN privateKey-----", "");
        publicKeyPEM = publicKeyPEM.replace(System.lineSeparator(), "");
        publicKeyPEM = publicKeyPEM.replace("-----END privateKey-----", "");
        
        byte[] encoded = Base64.decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("McEliece");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }
	
	public String decrypt(String encrytedDeviceID, PrivateKey key) throws InvalidKeyException, InvalidCipherTextException {
		byte[] msg = Base64.decode(encrytedDeviceID);
		McElieceCipher EnCipheredText = new McElieceCipher();
		McEliecePrivateKeyParameters GPKP = (McEliecePrivateKeyParameters) McElieceKeysToParams.generatePrivateKeyParameter(key);
		EnCipheredText.init(false, GPKP);
		byte[] DeviceID = EnCipheredText.messageDecrypt(msg);
		return new String(DeviceID);
	}
}
