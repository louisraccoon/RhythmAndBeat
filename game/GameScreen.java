package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameScreen extends JPanel  {
	
	int cnt=0; // ī��Ʈ���� 
	RythmandBeat main;	//RythmandBeatŬ���� �� ����
	int screenX=800;//���� ȭ�� ����
	int screenY=600;//���� ȭ�� ����
	
	//������۸�
	Image bufferImage;
	Graphics bufferGraphics;
	
	//Ÿ��Ʋ �κ�
	Image imgtitle=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/���1.png"));
	Image imgtitlestart=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/m2.png"));
	Image imgselect =Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/����ȭ��.png"));
	Image imgselector = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/�Ƿημ���.png"));
	Image imghelper = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/����1.png"));
	Image imgmaker= Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/������1.png"));
	int imgselector1 = 0;
	//����ȭ ĳ����
	int selX = 0;
	int selX2 = 0;
	Image imgCharacter1=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/�ε�.png"));
	Image imgCharacter2=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/�ظ�.png"));
	Image imgCharacter3=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/�Ƿη�.png"));
	Image imgCharacter4=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/����.png"));
	Image imgCharacter5=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/����.png"));
	Image imgCharacter6=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/ũ��.png"));
	Image imgCharacter7=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/��Ƽ.png"));
	Image imgCharacter8=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/����.png"));
	Image imgCharacter9=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/character/������.png"));

	//���Ǽ��úκ�drawSelect_m()
	Image imgselectmenu=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/menu/musicselect.png"));
	BMSCheck bmsselect;
	//�޴� �κ�
	int menuchoice =0; //�޴� ���� 0 ����Ÿ��Ʋ 1 ���Ӹ޴� 2 ���� ����
	
	//���ӽ���κ�
	Image imgplaybg=Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/skin/playbg.jpg"));//��������
	Image  bufimgplay = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/skin/playskin.png"));
	Image  imgplayeffect = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/skin/Effect.png"));
	Image  imgplayauto = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/skin/autoplay.png"));
	Image  imgresult = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/skin/imgresult.png"));
	//font�κ�
	Image  bufimgfont = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/skin/font.png"));
	Image  onoffimg = Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/skin/onoff.png"));
	
	 int exColor ;// ������� default ������
	
	 //���� �κ�

	 int playhour=0;
	
	 //bms ����̹���
	 int playbg = 0;
	 
	 
	 //drawPlaynote()
	int[] madinotenum; // ���� ��Ʈ�� ���ִ¹�ȣ ����
	int prnotenum; // ���� ��Ʈ��ȣ
	int madipointer = 0;// ���� ǥ������ ù��°����
	int madipointerlast = 4;// ���� ǥ������ ������ ����
	int lastpointer;
	Note madi;
	Note note;
	
	//drawEffect()
	ArrayList effectes ;
	//Ű ����
	public final static int S_PRESSED	=0x001;
	public final static int D_PRESSED	=0x002;
	public final static int F_PRESSED	=0x004;
	public final static int SPACE_PRESSED=0x008;
	public final static int J_PRESSED	=0x010;
	public final static int K_PRESSED = 0x020;
	
	GameScreen(RythmandBeat main) {
		this.main = main;
		
		
		
		setDoubleBuffered(false); // ������۸� on off
		//timer.start();

	}
	
	public void paint(Graphics g){
		if(bufferGraphics==null) {
			bufferImage=createImage(800,600);//���� ���۸��� ������ũ�� ���� ����. ���� paint �Լ� ������ �� ��� �Ѵ�. �׷��� ������ null�� ��ȯ�ȴ�.
			if(bufferImage==null) System.out.println("������ũ�� ���� ���� ����");
			else bufferGraphics=bufferImage.getGraphics();//������ũ�� ���ۿ� �׸��� ���� �׷��� ���ؽ�Ʈ ȹ��
			return;
		}
		update(g);
	}
	public void update(Graphics g){//ȭ�� ���ڰŸ��� ���̱� ����, paint���� ȭ���� �ٷ� ��ȭ���� �ʰ� update �޼ҵ带 ȣ���ϰ� �Ѵ�.
	
	
		if(bufferGraphics==null) return;
		drawpaint();//������ũ�� ���ۿ� �׸���
		g.drawImage(bufferImage,0,0,this);//������ũ�� ���۸� ����ȭ�鿡 �׸���.
	}
	public void drawpaint(){
		switch(menuchoice){
		case 0 : //Ÿ��Ʋ ȭ��
			drawTitle();
			break;
		case 1 : //���� �޴�
			drawSelect();
			break;
		case 2 : // ���� ����
			drawSelect_m();
			break;
		case 3 : // ���� �ε�
			drawLoading();
			break;
		case 4 : //���� ����
			drawPlaybg();
			drawPlaynote();
			drawPlayPicture(playbg);
			drawPlaypresskey();
			drawEffect();
			break;
		case 5 ://����
			drawHelper();
			break;
		case 6 ://������
			drawMaker();
			break;
		case 7://���ȭ��
			drawResult();
			break;
	};
	}
	public void drawTitle(){
		bufferGraphics.drawImage(imgtitle, 0, 0, this);
		if(cnt<=15){
			bufferGraphics.drawImage(imgtitlestart, 250, 450, this);
		}
		cnt=(cnt+1)%30;
	}
	
	public void drawSelect() {

		bufferGraphics.drawImage(imgselect, 0, 0, this);
		if(main.keybuff == KeyEvent.VK_UP && imgselector1 > 0){
			imgselector1 = imgselector1 - 60;
			selX = 0;	selX2 = 0;
			main.keybuff = 0;
		}else if(main.keybuff == KeyEvent.VK_DOWN && imgselector1 < 180){
			imgselector1 = imgselector1 + 60;
			selX = 0;	selX2 = 0;
			main.keybuff = 0;
		}
		bufferGraphics.drawImage(imgselector, 590, 345 + imgselector1,this);
		//bufferGraphics.drawImage(imgCharacter3,30,150,181,392,0+selX,0,151+selX,242,this); // �Ƿη� 1208
		//bufferGraphics.drawImage(imgCharacter7,310,200,457,692,0+selX2,0,147+selX2,235,this); // ��Ƽ 1176
		//bufferGraphics.drawImage(imgCharacter5,30,150,172,386,0+selX,0,142+selX,236,this); // ���� 1136
		//bufferGraphics.drawImage(imgCharacter8,30,150,263,415,0+selX,0,233+selX,365,this); // ����
		//bufferGraphics.drawImage(imgCharacter2,30,150,157,344,0+selX,0,127+selX,163,this); // �ظ� 1016
		//bufferGraphics.drawImage(imgCharacter9,30,150,248,493,0+selX,0,218+selX,343,this); // ������ 1744
		//bufferGraphics.drawImage(imgCharacter6,30,150,178,337,0+selX,0,148+selX,187,this); // ũ�� 1184
		if(imgselector1 == 0){
			bufferGraphics.drawImage(imgCharacter1,30,150,268,437,0+selX,0,238+selX,287,this); // �ε� 1904
			bufferGraphics.drawImage(imgCharacter4,310,200,485,485,0+selX2,0,175+selX2,285,this); // ���� 1400
		}else if(imgselector1 == 60){
			bufferGraphics.drawImage(imgCharacter3,30,150,181,392,0+selX,0,151+selX,242,this); // �Ƿη� 1208
			bufferGraphics.drawImage(imgCharacter7,310,200,457,435,0+selX2,0,147+selX2,235,this); // ��Ƽ 1176
		}else if(imgselector1 == 120){
			bufferGraphics.drawImage(imgCharacter9,30,150,248,493,0+selX,0,218+selX,343,this); // ������ 1744
			bufferGraphics.drawImage(imgCharacter6,310,200,458,387,0+selX2,0,148+selX2,187,this); // ũ�� 1184
		}else if(imgselector1 == 180){
			//bufferGraphics.drawImage(imgCharacter2,30,150,157,344,0+selX,0,127+selX,163,this); // �ظ� 1016
			bufferGraphics.drawImage(imgCharacter5,310,200,452,463,0+selX2,0,142+selX2,263,this); // ���� 1136
			bufferGraphics.drawImage(imgCharacter8,30,150,263,415,0+selX,0,233+selX,365,this); // ���� 1864
		}
		
	}
	public void drawHelper() {
		bufferGraphics.drawImage(imghelper, 0, 0,this);	
	}
	public void drawMaker(){
		bufferGraphics.drawImage(imgmaker, 0, 0,this);	
	}
	//���Ǽ���
	public void drawSelect_m() {
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.drawImage(bmsselect.titleimg, 75, 75,301,303, this);
		bufferGraphics.drawImage(imgselectmenu, 0, 0, this);
		bufferGraphics.setFont(new Font("����",  Font.PLAIN, 25));
		bufferGraphics.drawString(bmsselect.titlename, 154, 440);
		bufferGraphics.drawString(bmsselect.titlename, 154, 440);
		drawnumselect(main.prescore,174,470);//score�׸��� ��ġ
	}

	
	public void drawnumselect(int num, int x, int y) {
		//���ھ 4�ڸ������ ����
		int a,b,c,d;	//1000,100,10,1
		
		a = num/1000;
		b = (num-(a*1000))/100;
		c = (num-(a*1000)-(b*100))/10;
		d = num-(a*1000)-(b*100)-(c*10);
		if(a!=0){
			drawOnlynumselect(a,x,y);
		}
		if(b !=0 || a>0){
			drawOnlynumselect(b,x+23,y);
		}
		if(c !=0 || b>0){
			drawOnlynumselect(c,x+46,y);
		}
		
		drawOnlynumselect(d,x+69,y);
			
	}
	public void drawnumselect(int num,boolean check, int x, int y) {
		//���ھ 4�ڸ������ ����
		int a,b,c,d;	//1000,100,10,1
		
		a = num/1000;
		b = (num-(a*1000))/100;
		c = (num-(a*1000)-(b*100))/10;
		d = num-(a*1000)-(b*100)-(c*10);
		if(a!=0){
			drawOnlynumselect(a,x,y);
		}
		if(b !=0 || a>0){
			drawOnlynumselect(b,x+23,y);
		}
		if(c !=0 || b>0){
			drawOnlynumselect(c,x+46,y);
		}
		if(d !=0 || c>0){
		drawOnlynumselect(d,x+69,y);
		}
			
	}
	public void drawOnlynumselect(int num, int x, int y){
		switch(num){
		case 0:
			bufferGraphics.drawImage(bufimgfont, x, y, x+50,y+50, 
				360, 0, 400, 35, this);
			break;
		case 1:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+50,y+50,  
					0, 0, 40, 35, this);
			break;
		case 2:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+50,y+50, 
					40, 0, 80, 35, this);
			break;
		case 3:
			bufferGraphics.drawImage(bufimgfont,  x, y,  x+50,y+50,  
					80, 0, 120, 35, this);
			break;
		case 4:
			bufferGraphics.drawImage(bufimgfont,  x, y,  x+50,y+50, 
					120, 0, 160, 35, this);
			break;
		case 5:
			bufferGraphics.drawImage(bufimgfont,  x, y,  x+50,y+50, 
					160, 0, 200, 35, this);
			break;
		case 6:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+50,y+50,  
					200, 0, 240, 35, this);
			break;
		case 7:
			bufferGraphics.drawImage(bufimgfont,  x, y,  x+50,y+50, 
					240, 0, 280, 35, this);
			break;
		case 8:
			bufferGraphics.drawImage(bufimgfont,  x, y,  x+50,y+50, 
					280, 0, 320, 35, this);
			break;
		case 9:
			bufferGraphics.drawImage(bufimgfont,  x, y,  x+50,y+50, 
					320, 0, 360, 35, this);
			break;
		default:
		}
	}
	public void drawLoading(){//�ε� ȭ��
		bufferGraphics.drawImage(bmsselect.titleimg, 0, 0,800,600, this);
		
	}
	public void drawPlaybg(){     // ��� �̹��� �׸��� �Լ�
		
		bufferGraphics.drawImage(bufimgplay, 0,0,800,600, this);
		bufferGraphics.drawImage(imgplayauto, 350,0, this);
		if(main.autoplay){
			bufferGraphics.drawImage(onoffimg, 600,10,700,80,
					13,0,110,80, this);
		}else{
			bufferGraphics.drawImage(onoffimg, 600,10,700,80,
					120,0,239,80, this);
		}
		drawnum(main.score,460,466);//score�׸��� ��ġ
		drawnum(main.perfect,630,466);//perfect�׸��� ��ġ
		drawnum(main.maxcombo,460,508);//maxcombo�׸��� ��ġ
		drawnum(main.miss,620,508);//miss�׸��� ��ġ
		drawnumselect(main.combo,false,84,91);//��Ȯ���׸��� ��ġ
	//	bufferGraphics.setColor(Color.white);
		//bufferGraphics.drawString("playhour:"+playhour, 600, 550);
	}
	public void drawOnlynote(int key,  int y){//��Ʈ�׸����Լ�
		y = y/1000;
		switch(key){
			case 11://1��Ű �׸���
				bufferGraphics.drawImage(imgplayeffect, 62, y, 88,y+6, 
						70, 10, 101, 20, this);
				break;
			case 12:
				bufferGraphics.drawImage(imgplayeffect, 92, y, 116,y+6, 
						130, 10, 161, 20, this);
				break;
			case 13:
				bufferGraphics.drawImage(imgplayeffect, 120, y, 195,y+6, 
						190, 10, 265, 20, this);
				break;
			case 14:
				bufferGraphics.drawImage(imgplayeffect, 198, y, 226,y+6, 
						130, 10, 161, 20, this);
				break;
			case 15:
				bufferGraphics.drawImage(imgplayeffect, 227, y, 258,y+6, 
						70, 10, 101, 20, this);
				break;
			case 16:
				bufferGraphics.drawImage(imgplayeffect, 31, y, 59,y+6, 
						10, 10, 41, 20, this);
				break;
			case 99: //����
				drawOnlymadi( y);
				break;
				
		}
	}
	public void drawOnlymadi(int y){//�Ѹ��� �׸��� �Լ�
		bufferGraphics.setColor(Color.gray);
		bufferGraphics.drawLine(31, y+6, 258, y+6);

	}
	
	public void drawPlaynote(){ //ǥ������ ��� ��Ʈ �׸���

		madipointer=main.madipointer;
		lastpointer = main.lastpointer;
		
		for (int i = madipointer; i < lastpointer; i++) {
			for (int z = main.madinotenum[i]; z < main.madinotenum[i + 1]; z++) {
				note = (Note) main.notes.get(z);
				if (note.y >= 400000) {

				}  else if (note.key >= 11 && note.key <= 16 || note.key == 99) {
					drawOnlynote(note.key, note.y);
				}
			}
		}
		
	}
	
	public void drawPlayPicture(int playbg){//ǥ�õǴ� �̹��� �׸���
		if (playbg != 0) {
			bufferGraphics.drawImage(main.bms.BMP[playbg], 341, 85, 739,417,
					0, 0, 256, 256, this);
		}

	}
	public void drawPlaypresskey(){//���� Ű ǥ��
		if((main.key&S_PRESSED)!=0)
			bufferGraphics.drawImage(imgplayeffect, 30, 200, 61,400, 
					14, 230, 205, 430, this);
		if((main.key&D_PRESSED)!=0)
			bufferGraphics.drawImage(imgplayeffect, 61, 200, 92,400, 
					14, 230, 205, 430, this);
		if((main.key&F_PRESSED)!=0)
			bufferGraphics.drawImage(imgplayeffect, 90, 200, 120,400, 
					14, 230, 205, 430, this);
		
		if((main.key&SPACE_PRESSED)!=0)
			bufferGraphics.drawImage(imgplayeffect, 120, 200, 197,400, 
					14, 230, 205, 430, this);
		if((main.key&J_PRESSED)!=0)
			bufferGraphics.drawImage(imgplayeffect, 197, 200,228 ,400, 
					14, 230, 205, 430, this);
		if((main.key&K_PRESSED)!=0)
			bufferGraphics.drawImage(imgplayeffect, 227, 200, 260,400, 
					14, 230, 205, 430, this);
		
	}
	public void drawEffect(){//��Ʈ�� �������� ����Ʈ ȿ��ó��
		Effect effectbuf;
		int drawxeffect;//x��ǥ �ӽ�����
		for(int i=0 ; i<effectes.size() ; i++){
			effectbuf = (Effect)effectes.get(i);
			drawxeffect =effectbuf.cnt*100;
			switch (effectbuf.keynum) {
			case 1://1��Ű �϶� 
				bufferGraphics.drawImage(imgplayeffect, 28, 350, 117,439, 
						7+drawxeffect, 473, 98+drawxeffect, 575, this);
				break;
			case 2:
				bufferGraphics.drawImage(imgplayeffect, 59, 350, 143,439, 
						7+drawxeffect, 473, 98+drawxeffect, 575, this);
				break;
			case 3:
				bufferGraphics.drawImage(imgplayeffect, 99, 337, 222,450, 
						7+drawxeffect, 473, 98+drawxeffect, 575, this);
				break;
			case 4:
				bufferGraphics.drawImage(imgplayeffect, 166, 350, 264,439, 
						7+drawxeffect, 473, 98+drawxeffect, 575, this);
				break;
			case 5:
				bufferGraphics.drawImage(imgplayeffect, 199, 350, 307,439, 
						7+drawxeffect, 473, 98+drawxeffect, 575, this);
				break;
			case 6:
				bufferGraphics.drawImage(imgplayeffect, -20, 350, 87,439, 
						7+drawxeffect, 473, 98+drawxeffect, 575, this);
				break;
			}
			effectbuf.cnt++;
			if(effectbuf.cnt == 6){
				effectes.remove(i);
			}
		}
	}

	public void drawnum(int num, int x, int y) {
		//���ھ 4�ڸ������ ����
		int a,b,c,d;	//1000,100,10,1
		
		a = num/1000;
		b = (num-(a*1000))/100;
		c = (num-(a*1000)-(b*100))/10;
		d = num-(a*1000)-(b*100)-(c*10);
		if(a!=0){
			drawOnlynum(a,x,y);
		}
		if(b !=0 || a>0){
			drawOnlynum(b,x+17,y);
		}
		if(c !=0 || b>0){
			drawOnlynum(c,x+34,y);
		}
		
		drawOnlynum(d,x+51,y);
			
	}
	
	public void drawOnlynum(int num, int x, int y){
		switch(num){
		case 0:
			bufferGraphics.drawImage(bufimgfont, x, y, x+30,y+30, 
				360, 0, 400, 35, this);
			break;
		case 1:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					0, 0, 40, 35, this);
			break;
		case 2:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					40, 0, 80, 35, this);
			break;
		case 3:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					80, 0, 120, 35, this);
			break;
		case 4:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					120, 0, 160, 35, this);
			break;
		case 5:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					160, 0, 200, 35, this);
			break;
		case 6:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					200, 0, 240, 35, this);
			break;
		case 7:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					240, 0, 280, 35, this);
			break;
		case 8:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					280, 0, 320, 35, this);
			break;
		case 9:
			bufferGraphics.drawImage(bufimgfont,  x, y, x+30,y+30, 
					320, 0, 360, 35, this);
			break;
		default:
		}
	}
	public void drawResult(){//���ȭ��
		bufferGraphics.drawImage(imgresult, 0, 0,this);	
		bufferGraphics.drawImage(imgplayeffect, 58, 263, 299,492, 
				568, 0, 791, 227, this);
		if(main.accurate>=95){//��ũǥ��
			bufferGraphics.drawImage(imgplayeffect, 130, 301, 220,439, 
					343, 235, 436, 378, this);
		}else if(main.accurate>=90){
			bufferGraphics.drawImage(imgplayeffect, 130, 301, 220,439, 
					448, 231, 584, 371, this);
		}else if(main.accurate>=50){
			bufferGraphics.drawImage(imgplayeffect, 130, 301, 220,439, 
					582, 231, 698, 378, this);
		}else {
			bufferGraphics.drawImage(imgplayeffect, 140, 301, 230,439, 
					695, 235, 800, 378, this);
		}
		
		drawnumselect(main.maxcombo,513,191);//maxscore�׸��� ��ġ
		drawnumselect(main.miss,513,251);//miss�׸��� ��ġ
		drawnumselect(main.accurate,513,318);//��Ȯ���׸��� ��ġ
		drawnumselect(main.score,513,380);//�������׸��� ��ġ
		drawnumselect(main.prescore,513,447);//�ְ������׸��� ��ġ
	}

}

