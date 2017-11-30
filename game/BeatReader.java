package game;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
public class BeatReader {
	
	String fileName ;
	String path;			//���� ����ּ�
	
	FileReader fr,fr0 ;
	String[] splitline,splitline2;
	BMS hbms ;
	
	int tempmadi=0,lastmadi=0;
	int wavnum = 0, bmpnum=0;
	
	BufferedImage img, bufimg;
	
	//createMadi
	String madi ;
	int myMadi ;
	String tail ;	
	int myChannel ;
	public BeatReader(String fileName){
		hbms=null;
		 hbms = new BMS();
		 tempmadi=0;
		 lastmadi=0;
		 
		 
		try {
			this.fileName = fileName;
			//���� ��� �ּ� �˾ƿ��� 
			if (fileName.indexOf("/") != -1) {
				path = fileName.substring(0, fileName.lastIndexOf("/")+1);;
			}else if (fileName.indexOf("\\") != -1) {
				path = fileName.substring(0, fileName.lastIndexOf("\\")+1);;
			}
			System.out.println("path:"+path);
			fr0 = new FileReader(fileName);
			fr = new FileReader(fileName);

			BufferedReader br0 = new BufferedReader(fr0);
			BufferedReader br = new BufferedReader(fr);

			String line = "";

			for (int i = 1; (line = br0.readLine()) != null; i++) {
				if (line.indexOf("#") != -1) {//'#'�ִ��� üũ �׳� charAt�ҽ� �����߻�
					if (line.indexOf(":") != -1) {
						if (line.charAt(0) == '#') {
							if (line.charAt(6) == ':') {
								// System.out.println(i+"�� "+line);
								tempmadi = removeZero(line.substring(1, 4));
								lastmadi = (tempmadi > lastmadi) ? tempmadi	: lastmadi;	

							}
						}
					}
				}
			}

			lastmadi = lastmadi + 1;
			System.out.println("������ ���� :" + lastmadi);
			hbms.lastmadi = lastmadi;
			br0.close();
			//���� �ҷ����� ����
			hbms.madi = new ArrayList[lastmadi];
			hbms.channel = new ArrayList[lastmadi];
			for (int i = 0; i < lastmadi; i++) {
				hbms.madi[i] = new ArrayList(40);
				hbms.channel[i] = new ArrayList(40);
			}

			for (int i = 1; (line = br.readLine()) != null; i++) {
				if (line.indexOf("#") != -1) {// "#"�� ����ִ��� Ȯ��
					if (line.charAt(0) == '#') {// ���� �迭������ �������Ƿ� ���� ó��
						System.out.println(i + "�� " + line);
						splitline = line.split(" ");
					
						if (splitline[0].indexOf("#BMP") != -1) {
							if (line.substring(1, 4).equalsIgnoreCase("BMP")) {
								System.out.println("�׸� �߰�:" + line.substring(7));
								bmpnum = changehexint(line.charAt(4)) * 16;
								bmpnum += changehexint(line.charAt(5));
								//bmp ���
								
								if(splitline[1].indexOf(".bmp") != -1){

									img = ImageIO.read(new File(path+ line.substring(7)));
									bufimg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
									bufimg.getGraphics().drawImage(img, 0, 0,	null);
									hbms.BMP[bmpnum] = bufimg;
									System.out.println("bmp");
								}else{
								//jpg ���
									hbms.BMP[bmpnum] = Toolkit.getDefaultToolkit()
										.getImage(path+line.substring(7));
									System.out.println("jpg");
								}
								
							}
						}
						if (splitline[0].indexOf("#WAV") != -1) {
							if(line.substring(1, 4).equalsIgnoreCase("WAV")){
								try {
									System.out.println("wav�߰�"
											+ line.substring(4, 6) + ":"
											+ line.substring(7));
									// System.out.println(line.charAt(4));
									wavnum = changehexint(line.charAt(4)) * 16;
									wavnum += changehexint(line.charAt(5));
									// System.out.println("wavnum=" + wavnum);

									/*hbms.WAV[wavnum]  = new File(path
											+ line.substring(7));*/
									hbms.WAV[wavnum]  = new GameSound(path
											+ line.substring(7));
									
								}catch(Exception e){
									
								}
								
							}
								
						} else if (line.indexOf("#TITLE") != -1) {	//����
							if(line.substring(1, 6).equalsIgnoreCase("TITLE")){
								System.out.println("TITLE�߰�:" + line.substring(7));
								hbms.TITLE =line.substring(7);
							}
						} else if (line.indexOf("#ARTIST") != -1) {
							if(line.substring(1, 7).equalsIgnoreCase("ARTIST")){
								System.out.println("ARTIST�߰�:" + line.substring(8));
							}
						} else if (splitline[0].indexOf(":") != -1) {
							if (splitline[0].charAt(6) == ':') {
								//System.out.println(" ����߰�!");
								splitline2 = splitline[0].split(":");
								if (splitline2[1].length() == 2
										&& splitline2[1].indexOf("00") != -1) {
									
								}else{
									createMadi(splitline2[0], splitline2[1]);
								}
							}
						} else if (line.indexOf("#BPM") != -1) {
							if(line.substring(1, 4).equalsIgnoreCase("BPM")){
								System.out.println(line.substring(5));
								hbms.BPM = Double.parseDouble(line.substring(5));
							}
							
						}
					}
				}

			}
			fr0.close();
			fr.close();
			br.close();
			
			/*
			System.out.println("<-test ����->");
			System.out.println("10���� ����");
			System.out.println(hbms.madi[10].get(1) + "ä��");
			System.out.println(hbms.channel[10].get(1) + "����");
			*/

			System.out.println("�ҷ����� �Ϸ�");
		} catch (IOException e) {
			System.out.println("�Է¿���");
		}
	}
	public void createMadi(String head, String text){
		//���� ���� 
		madi = head.substring(1, 4);
		myMadi = removeZero(madi);
		System.out.println(myMadi+"����");
		
		//ä������
		tail =  head.substring(4);	
		myChannel =removeZero(tail);
		System.out.println(myChannel+"ä��");
		//System.out.println("�Էµ� text ����"+text);
		
		hbms.madi[myMadi].add(new Integer(myChannel));
		hbms.channel[myMadi].add(new String(text));
		
	}
	public int removeZero(String text){// String "001" -> int 1 ��ȯ�Լ�
		int startcheck ;
		for( startcheck =0; text.charAt(startcheck) =='0'  ; ){
			startcheck++;
			if(startcheck <=2)
			{
				break;
			}
		}
		return Integer.parseInt(text.substring(startcheck));
	}
	public static int changehexint(char temp){//16���� ���� 10����int������ ��ȯ
		switch(temp){
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'a':case 'A':
			return 10;
		case 'b':case 'B':
			return 11;
		case 'c':case 'C':
			return 12;
		case 'd':case 'D':
			return 13;
		case 'e':case 'E':
			return 14;
		case 'f':case 'F':
			return 15;
		default:
			return 0;
		}
	}
/*	public static void main(String[] args){
		//BeatReader br = new BeatReader("bms/hahaha song.bms");
	//	BeatReader br = new BeatReader("bms/shk73_amuse zone/amuse_5.bms");
		//BeatReader br =  new BeatReader("bms/taste/taste5.bms");
		BeatReader br = new BeatReader("bms/shk_A/A_e.bms");
		//BeatReader br = new BeatReader("bms/shk_friends/friends2_EZ.bms");
	}*/
}
