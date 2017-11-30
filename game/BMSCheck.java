package game;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;


import javax.imageio.ImageIO;

public class BMSCheck {
	BMSfilter bmsfilter = new BMSfilter();
	 String flist[] ;
	 File bmslist[]= new File[25];
	 int changer=0,size=0;
	 Image titleimg;
	 String path=null;//�����ּ�
	 String folderpath=null;//�����ּ�
	 String titlename="";
	BMSCheck(){
		
		File dir = new File("bms");//bms����������
		if( dir.isDirectory()){
			flist = dir.list();
		  for (int i=0; i<flist.length; i++)
		  {
			  
			  System.out.println(i + " " + flist[i]);
			  if(i<=24){
				  bmslist[i] = new File("bms/"+flist[i]);
			  }
		  }
		}
		size=flist.length;
		
		
	}
	public void search(){
		
		File list[] = bmslist[changer].listFiles(bmsfilter);
		path = ""+list[0];
		
	
		titleimg=smallBeatReader(path);
	}
	public Image smallBeatReader(String fileName){
		try{
			titlename = fileName;
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String[] splitline;
			String path="";
			BufferedImage img, bufimg;
			Image noimg=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/noimage.png"));
			if (fileName.indexOf("\\") != -1) {
				path = fileName.substring(0, fileName.lastIndexOf("\\")+1);;
			}
			for (int i = 1; (line = br.readLine()) != null; i++) {
				if (line.indexOf("#") != -1) {// "#"�� ����ִ��� Ȯ��
					if (line.charAt(0) == '#') {// ���� �迭������ �������Ƿ� ���� ó��
						//System.out.println(i + "�� " + line);
						splitline = line.split(" ");

						if (splitline[0].indexOf("#STAGEFILE") != -1) {
							line.substring(11);
							//System.out.println(path+line.substring(11));
							if(splitline[1].indexOf(".bmp") != -1){

								img = ImageIO.read(new File(path+line.substring(11)));
								bufimg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
								bufimg.getGraphics().drawImage(img, 0, 0,	null);
								br.close();
								fr.close();
								return bufimg;
								
							}else{
							//jpg ���
								return Toolkit.getDefaultToolkit()
									.getImage(path+line.substring(11));
								
							}
						}
					}
				}
			}
			return noimg;
		} catch (IOException e) {
			System.out.println("�Է¿���");
		}
		return null;
	}
	
	//������ �ҷ�����
	//@SuppressWarnings("deprecation")
	public  int GameDataLoad() {
		
		int HighScore=0;
		String[] splitline;
		try{
			String line = "";
			FileReader input = new FileReader("bms\\"+flist[changer]+"\\savedata.dat");
			BufferedReader br = new BufferedReader(input);
			for (int i = 1; (line = br.readLine()) != null; i++) {
				if (line.indexOf("#") != -1)
				{
					splitline = line.split(" ");
					if (splitline[0].indexOf("#score") != -1) {
						return HighScore=Integer.parseInt(splitline[1]);
						
					}
				}
			}
			br.close();
			input.close();
		}catch(IOException e){
			
	   		System.out.println("�����ͷε� ����");
	   	}
		return HighScore;
	}
	//����������
	public void GameDataSave(int HighScore, int MaxCombo) {
		try{
		FileWriter output = new FileWriter("bms\\"+flist[changer]+"\\savedata.dat");
		BufferedWriter bw = new BufferedWriter(output);
		bw.write("#score "+HighScore);
		bw.newLine();
		bw.write("#maxcombo "+MaxCombo);
	    bw.close();
		output.close();
		}catch(IOException e){
			
		}
	}
	//������ ���翩��Ȯ��
	public boolean fileexists() {
		File FileExist = new File("bms\\"+flist[changer]+"\\savedata.dat"); 
        return FileExist.exists(); 
    }
	public static void main(String[] args) {
		BMSCheck test = new BMSCheck();
	}
}

class BMSfilter implements FilenameFilter{
	public boolean accept(File dir, String name) {
		return name.endsWith(".bms");
	}
}
