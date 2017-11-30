package game;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.*;
import java.util.*;


//substring(0,1)�� ���ڿ��� 0��° �ѱ��ڸ����. �� �Ű������ձ��� �� ǥ�� 
//���ڿ��� �׻� == �ƴ϶� indexof�� ��

public  class RythmandBeat extends JFrame  implements
Runnable, KeyListener{
	long pretime;//��������� �����ð� ���
	//long delay=33;//������ ���� ������ 1000ms=1s,���� �ʴ� 30fps
	long delay=50;//������ ���� ������ 1000ms=1s,17/1000�� = 58 (������/��)
	long fpscheck = 0; //�ʴ� fps üũ
	
	Timer timer;
	
	String curDir = System.getProperty("user.dir"); 
	
	boolean mainrun=true;//���� ������ �۵� ����
	Thread mainThread = new Thread(this);//���� ������ ����
	
	GameScreen gamescreen = new GameScreen(this);//����ȭ��׸��ⰴü����
	DrawThread drawThread = new DrawThread(gamescreen);//ȭ�龲�������
	
	GameSound backmusic = new GameSound(getClass().getResource("bg/PororoED.wav"));
	
	int status =0;	//�������μ���
	
	//s_music_process()
	BMSCheck bmsselect = new BMSCheck();
	String filename = null;
	 //bms �κ�
	 BeatReader br;//bms ������
	 BMS bms ;// br�κ��� bms ���� ��ü
	ArrayList notes = new ArrayList();//ǥ�õǴ� ��Ʈ ����
	
	
	int sizeshort = 1000; // 1/1000;���� �ڹ�ǥ����ġ�� ���ӻ� �Ÿ� ���
	int endline = 500*sizeshort; //���ӻ� ��Ʈ ���κ� 
	//int bmsspeed = 6000; //�������� �ӵ�
	int bmsspeed = 2000; //�������� �ӵ�
	int endlinebms = (endline/bmsspeed-2)*bmsspeed; //�ӵ��� ���� ���ӻ��Ʈ���κ�����
	
	
	
	
	//madisprocess
	int channel = 0;	//ä������
	int madiywh= 0;//���� y����ġ
	int textlength= 0 ; //text ���� ����
	int notesum=1;	//�Ѹ��� ��Ʈ����
	int notelength=0;//��Ʈ ���� ����
	int madilength=400000;//���� ����
	int madinum = 0;//���� ��ȣ
	int timelength=0;//����� ���� ����
	String text = "";
	int size ; //��Ʈ������� �ѹ� �а��� �״��
	Note madi;
	Note note;
	int[] madinotenum; // ���� ��Ʈ�� ���ִ¹�ȣ ����
	int prnotenum; // ���� ��Ʈ��ȣ
	int madipointer = 0;// ���� ǥ������ ù��°����
	int madipointerlast = 1;// ���� ǥ������ ������ ����
	int lastpointer;
	boolean autoplay=false; //�ڵ���� on, off
	Effect effectbuf;//�­����� �̹��� �ӽ�Ŭ����
	ArrayList effectes = new ArrayList();//ǥ�õǴ� ����Ʈȿ�� ����
	int lastcheck =0;
	
	//�޸� �� ����
	long heapSize;
	String heapSizeMB;
	
	//���Ǵ� Ű��ȣ ��
	
	//keyprocess
	int key=0;	// Ű ���尪
	int keybuff = 0; // Ű�̺�Ʈ �� ����
	public final static int S_PRESSED	=0x001;
	public final static int D_PRESSED	=0x002;
	public final static int F_PRESSED	=0x004;
	public final static int SPACE_PRESSED=0x008;
	public final static int J_PRESSED	=0x010;
	public final static int K_PRESSED = 0x020;
	boolean lockkey[] = new boolean[7];
	//crashprocess
	int choicekey[] = new int[7] ; //�� Ű�� �����ϴ� �ӽ� ��Ʈ��ȣ���
	Note notecrash;
	
	//game ���� ���
	int score;	  //����
	int combo;		
	int perfect; //����Ƚ��
	int miss;	 //Ʋ��Ƚ��
	int maxcombo;
	int accurate;//��Ȯ��
	
	int prescore=0;//���������� �ְ�����
	
	RythmandBeat(){
		
		add(gamescreen);
		setResizable(false);
		setTitle("Rythm and Beat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLocationRelativeTo(null);
		setVisible(true);
		
		System.out.println(curDir);
		addKeyListener(this);//Ű������ �߰�
		//mainThread.setPriority(9);
		mainThread.start();//���ξ����� ����
		//drawThread.setPriority(4);
		drawThread.start();
		backmusic.play();
	}
	public void run(){
		try {
			while (mainrun) {
				//System.out.println("������ �۵���");
				pretime = System.currentTimeMillis();
				process();
				
				
				//�����嵿�� �ð� �����ϰ� ����
				if (System.currentTimeMillis() - pretime < delay){
					Thread.sleep(delay - System.currentTimeMillis() + pretime);
				}
				

			}
		} catch (Exception e) {

		}
	}

	public void process(){ 
		switch(status){
		case 0: //Ÿ��Ʋ
			title_process();
			break;
		case 1: //�޴�
			select_process(); 
			break;
		case 2: //���� ����
			s_music_process();
			break;
		case 3://���ӽ��� �ʱ�ȭ //�ε�
			init_Playprocess();
			break;
		case 4://���ӽ��� ����
			madisprocess();
			keyprocess();
			break;
		case 5://���Ӱ��ȭ��
			resultprocess();
			break;
		}
		
		
		//System.out.println("���μ��� �۵���");
	}
	//�޴� ����
	private void select_process() {
		
		
		if(keybuff == KeyEvent.VK_ENTER){
			keybuff=0;
			if(gamescreen.imgselector1 == 0){
				status = 2;
				 gamescreen.bmsselect=bmsselect;
				gamescreen.menuchoice = 2;
			}
			else if(gamescreen.imgselector1 == 60)
				gamescreen.menuchoice = 6;
			else if (gamescreen.imgselector1 == 120)
				gamescreen.menuchoice = 5;
			else if (gamescreen.imgselector1 == 180)
				System.exit(0);
		} else if (keybuff == KeyEvent.VK_SPACE) {
			keybuff = 0;
			if (gamescreen.imgselector1 == 0) {
				status = 2;
				 gamescreen.bmsselect=bmsselect;
				gamescreen.menuchoice = 2;
			} else if (gamescreen.imgselector1 == 60)
				gamescreen.menuchoice = 6;
			else if (gamescreen.imgselector1 == 120)
				gamescreen.menuchoice = 5;
			else if (gamescreen.imgselector1 == 180)
				System.exit(0);
		}else if (keybuff == KeyEvent.VK_ESCAPE){
			gamescreen.menuchoice = 1;
		}
		
		if(gamescreen.imgselector1 == 0){
			gamescreen.selX += 238;
			if(gamescreen.selX == 1904)
				gamescreen.selX = gamescreen.selX % 1904;
			gamescreen.selX2 += 175;
			if(gamescreen.selX2 == 1400)
				gamescreen.selX2 = gamescreen.selX2 % 1400;
		}
		else if(gamescreen.imgselector1 == 60){
			gamescreen.selX += 151;
			if(gamescreen.selX == 1208)
				gamescreen.selX = gamescreen.selX % 1208;
			gamescreen.selX2 += 147;
			if(gamescreen.selX2 == 1176)
				gamescreen.selX2 = gamescreen.selX2 % 1176;
		}else if(gamescreen.imgselector1 == 120){
			gamescreen.selX += 218;
			if(gamescreen.selX == 1744)
				gamescreen.selX = gamescreen.selX % 1744;
			gamescreen.selX2 += 148;
			if(gamescreen.selX2 == 1184)
				gamescreen.selX2 = gamescreen.selX2 % 1184;
		}else if(gamescreen.imgselector1 == 180){
		//	gamescreen.selX += 127;
		//	if(gamescreen.selX == 1016)
		//		gamescreen.selX = gamescreen.selX % 1016;
			gamescreen.selX2 += 142;
			if(gamescreen.selX2 == 1136)
				gamescreen.selX2 = gamescreen.selX2 % 1136;
			gamescreen.selX += 233;
			if(gamescreen.selX == 1864)
				gamescreen.selX = gamescreen.selX % 1864;
		}
	}
	private void title_process() {
		
		if(keybuff == KeyEvent.VK_SPACE|| keybuff==KeyEvent.VK_ENTER){
			status = 1;
			keybuff=0;
			gamescreen.menuchoice = 1; 
		}
	}
	//�޴� ��
	//���� ���� ����
	private void s_music_process() {
		bmsselect.search();
		if(bmsselect.fileexists()){
			prescore = bmsselect.GameDataLoad();	
		}
		if(keybuff == KeyEvent.VK_RIGHT){
			bmsselect.changer=(bmsselect.changer+1)%bmsselect.size;
			prescore=0;
			keybuff=0;
		}
		else if(keybuff == KeyEvent.VK_LEFT){
			if (bmsselect.changer == 0) {
				bmsselect.changer += bmsselect.size - 1;
			} else {
				bmsselect.changer = (bmsselect.changer - 1) % bmsselect.size;
			}
			prescore=0;
			keybuff=0;
		}
		else if(keybuff == KeyEvent.VK_ENTER){
			keybuff=0;
			status=3;
			filename = bmsselect.path;
			gamescreen.menuchoice = 3;
			backmusic.stop();
		}
		else if(keybuff == KeyEvent.VK_ESCAPE){
			keybuff=0;
			status=1;
			gamescreen.menuchoice = 1;
			
		}
		heapSize   = Runtime.getRuntime().totalMemory();
		heapSizeMB = (heapSize / (1024*1024)) + "MB";
		System.out.println(heapSizeMB);
	}
	//���ӽ��� ����
	public void init_Playprocess(){//���ӽ����� bms �ε�
		delay =10;//������ �ӵ� ����
		System.out.println("�ʱ����ҽ� ����");
		//�����ʱ�ȭ
		 channel = 0;	//ä������
		 madiywh= 0;//���� y����ġ
		 textlength= 0 ; //text ���� ����
		 notesum=1;	//�Ѹ��� ��Ʈ����
		notelength=0;//��Ʈ ���� ����
		 madinum = 0;//���� ��ȣ
		 timelength=0;//����� ���� ����
		 text = "";
		madipointer = 0;// ���� ǥ������ ù��°����
		madipointerlast = 1;// ���� ǥ������ ������ ����
		
		notes.clear();
		effectes.clear();
		//game ���� ���
		score=0;
		 combo=0;
		perfect=0;
		 maxcombo=0;
		 miss=0;
		 lastcheck=0;//������ ���� �˻�
		lastpointer = madipointerlast;
		gamescreen.playbg=0;
		
		System.out.println(filename);
		br = null;
		br = new BeatReader(filename);
		System.out.println("BeatReader����Ϸ�");
		
		
		
		bms = br.hbms; // bms ��ü ��ƿ���
		madinotenum = new int[bms.lastmadi];//���� ��ġ ���
		prnotenum = 0;
		//bms ��Ʈ �о����
		for (madinum = 0; madinum < bms.lastmadi; madinum++) {
			madinotenum[madinum]= prnotenum;//���� ������ ���
			note = new Note(99, madiywh );
			notes.add(note);
			prnotenum++;
			for (int i = 0; i < bms.madi[madinum].size(); i++) {
				channel = (Integer) bms.madi[madinum].get(i);
				text = (String) bms.channel[madinum].get(i);
				textlength=text.length();
				if( textlength>= 2){
					notesum = textlength / 2;
				}else{
					notesum =1;
				}
				
				//System.out.println("�����ȣ : "+madinum+"notesum="+notesum);
				notelength = madilength / notesum;
				int z=0;
				switch (channel) {
					case 1:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(1, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					case 4:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(4, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					case 11:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(11, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					case 12:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(12, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					case 13:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(13, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					case 14:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(14, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					case 15:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(15, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					case 16:
						for (int j = 0; j < text.length(); j+=2,z++) {
							if(text.substring(j, j + 2).indexOf("00")!= -1){
								continue;
								
							}
							note = new Note(16, madiywh - (notelength * z),
									text.substring(j, j + 2));
							notes.add(note);
							prnotenum++;
						}
						break;
					default:
					}
					
			}
		
			//madiywh -= madilength; //���𸶴� ��ġ����
			
			//System.out.println("�ʱ����ҽ��Ϸ�");
		}
		//���� �ʱ�ȭ
		for(int i =0 ;i<7 ; i++){
			choicekey[i]=0;
			lockkey[i]= false;
		}
		
		gamescreen.effectes = effectes;
		size = notes.size();// ��Ʈ ���� �޾ƿ���
		System.out.println("�Ϻ��ʱ����ҽ� �Ϸ�");
		status = 4;//���ӽ���
		gamescreen.menuchoice = 4;//�׸������μ���
		
	}
	void endPlay(){ //�ٽ� �ٸ��� ����� ���� �ʱ�ȭ , ���ǰ��ȭ������ �Ѿ
		bmsselect.GameDataSave(score, maxcombo);
		if(perfect != 0 ||miss !=0 ){
			accurate=100*perfect/(perfect+miss);
		}else{
			accurate=0;
		}
		bms=null;
		gamescreen.menuchoice = 7;
		status = 5;	
		delay = 50;
	}
	public void madisprocess(){ //���� ǥ������ ���� �׸���
		

		
		lastpointer = madipointerlast;
		for (int i = madipointer; i < lastpointer; i++) {
			madi = (Note) notes.get(madinotenum[i]);
			if(madi.y> 850000){//850000
				madipointer++;
			}
			else if (madi.y >= madilength+bmsspeed) {
				
			} else if (madi.y >= madilength) {
				if (madipointerlast != bms.lastmadi-1) {
					madipointerlast++;
				}
				System.out.println("madilast:" + madipointerlast);
				System.out.println("madifirst:" + madipointer);
			} 
			if(bms.lastmadi-1 == madipointerlast){//������ ���ȭ�� ���
				if(lastcheck ==1){
				
					 timer = new Timer(); 
				        timer.schedule(new TimerTaskexit(), 5*1000); 
				    
				}else{
					lastcheck++;
				}
				
			}
			for (int z = madinotenum[i]; z < madinotenum[i + 1]; z++) {
				note = (Note) notes.get(z);
				if (note.y >= 500000) {
					
				}else if(note.y >= 407000){
					
				}
				else if(note.y>=400000){
					if( note.key == 1){
						bms.WAV[note.fnum].play();
						note.key =80; //���ǰ���������ä��
					}else if (note.key == 4) {	
						gamescreen.playbg = note.fnum;
						note.key =80; //���ǰ���������ä��
					}else if(autoplay){
						if(note.key>=11 && note.key<=16){
							bms.WAV[note.fnum].play();
							note.key =80; //���ǰ���������ä��
						}
					}
					
					else if(note.key >= 11 && note.key <= 16){
						
						if(note.key ==11){
							if ((key&D_PRESSED)!=0) {
								effectbuf = new Effect(1);
								effectes.add(effectbuf);
								perfect++;
								combo++;
							}else{
								miss++;
								combo=0;
							}
						}else if(note.key ==12){
							if ((key & F_PRESSED)!=0) {
								effectbuf = new Effect(2);
								effectes.add(effectbuf);
								perfect++;
								combo++;
							}else{
								miss++;
								combo=0;
							}
						}else if(note.key ==13){
							if ((key & SPACE_PRESSED)!=0) {
								effectbuf = new Effect(3);
								effectes.add(effectbuf);
								perfect++;
								combo++;
							}else{
								miss++;
								combo=0;
							}
						}else if(note.key ==14){
							if ((key & J_PRESSED)!=0) {
								effectbuf = new Effect(4);
								effectes.add(effectbuf);
								perfect++;
								combo++;
							}
						}else if(note.key ==15){
							if ((key & K_PRESSED) != 0) {
								effectbuf = new Effect(5);
								effectes.add(effectbuf);
								perfect++;
								combo++;
							}else{
								miss++;
								combo=0;
							}
						}else if(note.key ==16){
							if ((key & S_PRESSED) !=0 ) {
								effectbuf = new Effect(6);
								effectes.add(effectbuf);
								perfect++;
								combo++;
							}else{
								miss++;
								combo=0;
							}
						}
						maxcombo= maxcombo > combo ? maxcombo : combo;
						note.key =80; //���ǰ���������ä��
					}
				}
				else if (note.y >= 394000) {
					
					
					 if(note.key ==11){
						choicekey[1] =z;
					}else if(note.key ==12){
						choicekey[2] =z;
					}else if(note.key ==13){
						choicekey[3] =z;
					}else if(note.key ==14){
						choicekey[4] =z;
					}else if(note.key ==15){
						choicekey[5] =z;
					}else if(note.key ==16){
						choicekey[6] =z;
					}
				} 
				note.changeY(bmsspeed);
			}
			score = perfect*5;					
		}
		
		
		heapSize   = Runtime.getRuntime().totalMemory();
		heapSizeMB = (heapSize / (1024*1024)) + "MB";
		System.out.println(heapSizeMB);
	}
	public void keyprocess(){
		switch(key){
		case S_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
				System.out.println("s����");
			} 

			break;
		case D_PRESSED:
			
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
				System.out.println("d����");
			} 	
			break;
		case F_PRESSED:
			
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
				System.out.println("f����");
			} 	
			break;
		case SPACE_PRESSED:
			
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
				System.out.println("space����");
			} 	
			break;
		case J_PRESSED:
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
				System.out.println("j����");
			} 	
			break;	
		case K_PRESSED:
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
				System.out.println("k����");
			} 	
			break;
			// �ΰ� Ű �ߺ�
		case S_PRESSED|D_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			System.out.println("s�� d����");break;
		case S_PRESSED|F_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			System.out.println("s�� d����");break;	
		case S_PRESSED|SPACE_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 	
			System.out.println("s�� space����");break;
		case S_PRESSED|J_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("s�� j����");break;
		case S_PRESSED|K_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 	
			System.out.println("s�� k����");break;
		case D_PRESSED|F_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			System.out.println("d�� f����");break;
		case D_PRESSED|SPACE_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 	
			System.out.println("d�� space����");break;
		case D_PRESSED|J_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("d�� j����");break;
		case D_PRESSED|K_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			}
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 	
			System.out.println("d�� k����");break;
		case F_PRESSED|SPACE_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			System.out.println("F�� space����");break;
		case F_PRESSED|J_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("F�� j����");break;
		case F_PRESSED|K_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("F�� k����");break;
		case SPACE_PRESSED|J_PRESSED:
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 
			System.out.println("space�� j����");break;
		case SPACE_PRESSED|K_PRESSED:
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("space�� k����");break;
		case J_PRESSED|K_PRESSED:
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("j�� k����");break;

		
		}
	}
	public void crashprocess(int key){
		switch(key){
		case S_PRESSED: 
			if (choicekey[6] != 0) {
				notecrash = (Note) notes.get(choicekey[6]);
				bms.WAV[notecrash.fnum].play();
			}
			break;
		case D_PRESSED: 
			if (choicekey[1] != 0) {
				notecrash = (Note) notes.get(choicekey[1]);
				bms.WAV[notecrash.fnum].play();
			}
			break;
		case F_PRESSED:
			if (choicekey[2] != 0) {
				notecrash = (Note) notes.get(choicekey[2]);
				bms.WAV[notecrash.fnum].play();
			}
			break;
		case SPACE_PRESSED:
			if (choicekey[3] != 0) {
				notecrash = (Note) notes.get(choicekey[3]);
				bms.WAV[notecrash.fnum].play();
			}
			break;
		case J_PRESSED:
			if (choicekey[4] != 0) {
				notecrash = (Note) notes.get(choicekey[4]);
				bms.WAV[notecrash.fnum].play();
			}
			break;	
		case K_PRESSED:
			if (choicekey[5] != 0) {
				notecrash = (Note) notes.get(choicekey[5]);
				bms.WAV[notecrash.fnum].play();
			}
			break;
			
		}
	}
	public void resultprocess(){//���ȭ��
		if(keybuff == KeyEvent.VK_SPACE|| keybuff==KeyEvent.VK_ENTER||keybuff==KeyEvent.VK_ESCAPE){
			status = 2;
			keybuff=0;
			gamescreen.menuchoice = 2; 
		}
	}
	//keyListener ����
	public void keyPressed(KeyEvent e){
		if (status == 4) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_S:
				key |= S_PRESSED;
				break;
			case KeyEvent.VK_D:
				key |= D_PRESSED;
				break;
			case KeyEvent.VK_F:
				key |= F_PRESSED;
				break;
			case KeyEvent.VK_SPACE:
				key |= SPACE_PRESSED;
				break;
			case KeyEvent.VK_J:
				key |= J_PRESSED;
				break;
			case KeyEvent.VK_K:
				key |= K_PRESSED;
				break;
			case KeyEvent.VK_F3:
				if(autoplay){
					autoplay= false;
				}else{
					autoplay=true;
				}
				break;
			case KeyEvent.VK_ESCAPE:
				
				endPlay();
				keybuff = 0;
				System.out.println("esc����");

			}
		}
		else if(status!=4) {
			keybuff=e.getKeyCode();
			//System.out.println(keybuff);
		}
		
	}
	public void keyReleased(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_S:
			key&=~S_PRESSED;
			lockkey[6]=false;
			break;
		case KeyEvent.VK_D:
			key&=~D_PRESSED;
			lockkey[1]=false;
			break;
		case KeyEvent.VK_F:
			key&=~F_PRESSED;
			lockkey[2]=false;
			break;
		case KeyEvent.VK_SPACE:
			key&=~SPACE_PRESSED;
			lockkey[3]=false;
			break;
		case KeyEvent.VK_J:
			key&=~J_PRESSED;
			lockkey[4]=false;
			break;
		case KeyEvent.VK_K:
			key&=~K_PRESSED;
			lockkey[5]=false;
			break;
		
		}
	}
	public void keyTyped(KeyEvent e){
		
	}
	//keyListener ����
	
	class TimerTaskexit extends TimerTask { 
        public void run() { 
        	endPlay();//�����������ϰ� ���ȭ�������̵�
            timer.cancel(); //timer thread ���� 
        } 
    } 

	
	public static void main(String[] args){
		RythmandBeat t1 = new RythmandBeat();
	}
}

class Note{
	int key;	// ä�� ����
	int y;
	
	int fnum=0;	//	�ش��ϴ� ���Ϲ�ȣ(wav,bmp) 
	
	Note(int key , int y){//���� ���� ������
		this.key=key;	//����� Ű���� 99
		this.y=y;
	}
	Note(int key, int y, String mnum){//�Ϲ� ��Ʈ�� ���� ������
		this.key=key;
		this.y=y;
		
		fnum =BeatReader.changehexint(mnum.charAt(0)) * 16;
		fnum +=BeatReader.changehexint(mnum.charAt(1));	
	}
	public void changeY(int y){
		this.y += y;
	}
}
class Effect{//��Ʈ �������� Effect
	int keynum;//Ű��ȣ
	int cnt;// 6���� �̹���(0~5��)6�Ǹ� ����
	Effect(int keynum){
		this.keynum=keynum;
		this.cnt = 0;
	}
	
}
class DrawThread extends Thread{
	GameScreen gamescreen;
	long pretime;//������ ������ �����ϱ� ���� �ð� üũ��
	long delay=33;//������ ���� ������ 1000ms=1s,���� �ʴ� 30fps
	DrawThread(GameScreen gs){
		gamescreen = gs;
	}
	public void run(){
		try{
			while(true){
				pretime = System.currentTimeMillis();
				gamescreen.repaint();
				if (System.currentTimeMillis() - pretime < delay){
					Thread.sleep(delay - System.currentTimeMillis() + pretime);
				}
			}
		}catch(Exception e){
			
		}
	}
}

