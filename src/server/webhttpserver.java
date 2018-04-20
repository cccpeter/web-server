package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Properties;

public class webhttpserver {
	private static final String PROPERTIES_NAME = "config/config.properties";
	public static String result=Thread.currentThread().getContextClassLoader().getResource(".").getPath()+"webapp";
	private static String PORT = null;
	private static String ADDRESS = null;
	private static String VERSION = null;
	private static int counter = 0;

	public webhttpserver() {

	}

	public void WebServer() {
		HashMap<String, String> config = new HashMap<>();
		ServerSocket server = null;
		Socket client = null;
		config = getproperties();
		for (String key : config.keySet()) {
			System.out.println(key + ":" + config.get(key));
		}
		try {
			server = new ServerSocket(Integer.parseInt(PORT));
			System.out.println("服务器启动PORT为:" + PORT);
			while (true) {
				client = server.accept();
				counter++;
				System.out.println("当前是线程："+counter);
				new CommunicationThread(client).start();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static HashMap<String, String> getproperties() {
		HashMap<String, String> config = new HashMap<>();
		FileInputStream in = null;
		try {
			Properties properties = new Properties();
			// 获取配置文件的目录并读取加载文件
			in = new FileInputStream(
					Thread.currentThread().getContextClassLoader().getResource(".").getPath() + PROPERTIES_NAME);
			properties.load(in);
			PORT = properties.getProperty("PORT");
			config.put("PORT", PORT);
			ADDRESS = properties.getProperty("ADDRESS");
			config.put("ADDRESS", ADDRESS);
			VERSION = properties.getProperty("VERSION");
			config.put("VERSION", VERSION);
			System.out.println("读取配置信息成功");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("读取配置信息失败！");
		}
		return config;
	}

	public static void main(String[] args) {
		webhttpserver httpserver = new webhttpserver();
		httpserver.WebServer();
	}
}

class CommunicationThread extends Thread {
	Socket client = null;

	public CommunicationThread(Socket client) {

		this.client = client;

	}

	public void run() {

		try {

			String destIp = client.getInetAddress().toString();

			String destPort = client.getPort() + "";

			System.out.println("请求来源IP为：" + destIp + "----请求响应端口为：" + destPort);

			BufferedReader reader = new BufferedReader(new InputStreamReader(

			client.getInputStream()));

			String inputLine = reader.readLine();

			System.out.println("inputLine为：" + inputLine);

			String resourceUrl = inputLine.substring(

			inputLine.indexOf('/') + 1, inputLine.lastIndexOf('/') - 5);

			int i = 0;

			while ((inputLine = reader.readLine()) != null) {

				if (inputLine.equals("")) {

					break;

				}

				i++;

				System.out.println("第" + i + "行http头部信息为: " + inputLine);

			}

			resourceUrl = URLDecoder.decode(resourceUrl, "UTF-8");

			System.out.println("请求资源为：" + resourceUrl);

			System.out.println(webhttpserver.result+"/"+resourceUrl);
			
			File file = new File("D:/workspaces-itcast/template/webserver/bin/webapp/"+resourceUrl);

			PrintStream outstream = new PrintStream(client.getOutputStream());

			if (file.exists() && !file.isDirectory()) {

				System.out.println("文件存在");

				outstream.println("HTTP/1.0 200 OK");

				outstream.println("MIME_version:1.0");

				outstream.println("Content_Type:text/html");

				outstream.println("Content_Length:" + file.length());

				outstream.println("");

				transportFile(outstream, file);

				outstream.flush();

			} else {

				String notFound = "<html><head><title>Not Found</title></head><body><h1>404 file not found</h1></body></html>";

				outstream.println("HTTP/1.0 404 no found");

				outstream.println("Content_Type:text/html");

				outstream.println("Content_Length:" + notFound.length() + 2);

				outstream.println("");

				outstream.println(notFound);

				outstream.flush();

			}

			// outstream.close();

			long b = 1;

			while (b < 3600000) {

				b++;

			}

			reader.close();

			client.close();

		} catch (Exception e) {

			// System.out.println("run方法内出错：" + e.getMessage());

		} finally {

			/*
			 * 
			 * try { //client.close(); System.out.println("本次请求成功关闭！"); } catch
			 * 
			 * (IOException e) { e.printStackTrace(); }
			 * 
			 */

		}
	}

	public void transportFile(PrintStream ps, File file) {

		try {

			DataInputStream in = new DataInputStream(new FileInputStream(file));

			byte temp[] = new byte[10240];

			int a = 0;

			while ((a = in.read(temp, 0, 10240)) != -1) {

				ps.write(temp);

				ps.flush();

			}

			in.close();

		} catch (IOException e) {

			System.out.println("文件传输中出错,错误信息为：" + e.getMessage());

		} finally {

			ps.close();

			System.out.println("传输完毕");

		}

	}
}
