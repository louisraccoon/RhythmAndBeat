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


//substring(0,1)Àº ¹®ÀÚ¿­Áß 0¹øÂ° ÇÑ±ÛÀÚ¸¸Ãâ·Â. µÚ ¸Å°³º¯¼ö¾Õ±îÁö ¸¸ Ç¥½Ã 
//¹®ÀÚ¿­Àº Ç×»ó == ¾Æ´Ï¶ó indexof·Î ºñ±³

public  class RythmandBeat extends JFrame  implements
Runnable, KeyListener{
	long pretime;//¾²·¹µå½ÇÇà ÀÌÀü½Ã°£ ±â¾ï
	//long delay=33;//¾²·¹µå °£°Ý µô·¹ÀÌ 1000ms=1s,ÇöÀç ÃÊ´ç 30fps
	long delay=50;//¾²·¹µå °£°Ý µô·¹ÀÌ 1000ms=1s,17/1000ÃÊ = 58 (ÇÁ·¹ÀÓ/ÃÊ)
	long fpscheck = 0; //ÃÊ´ç fps Ã¼Å©
	
	Timer timer;
	
	String curDir = System.getProperty("user.dir"); 
	
	boolean mainrun=true;//¸ÞÀÎ ¾²·¹µå ÀÛµ¿ À¯¹«
	Thread mainThread = new Thread(this);//¸ÞÀÎ ¾²·¹µå »ý¼º
	
	GameScreen gamescreen = new GameScreen(this);//°ÔÀÓÈ­¸é±×¸®±â°´Ã¼»ý¼º
	DrawThread drawThread = new DrawThread(gamescreen);//È­¸é¾²·¹µå»ý¼º
	
	GameSound backmusic = new GameSound(getClass().getResource("bg/PororoED.wav"));
	
	int status =0;	//ÁøÇàÇÁ·Î¼¼½º
	
	//s_music_process()
	BMSCheck bmsselect = new BMSCheck();
	String filename = null;
	 //bms ºÎºÐ
	 BeatReader br;//bms ¸®´õ±â
	 BMS bms ;// br·ÎºÎÅÍ bms ´ãÀ» °´Ã¼
	ArrayList notes = new ArrayList();//Ç¥½ÃµÇ´Â ³ëÆ® °ü¸®
	
	
	int sizeshort = 1000; // 1/1000;½ÇÁ¦ ÀÚ¹ÙÇ¥½ÃÀ§Ä¡¿Í °ÔÀÓ»ó °Å¸® ºñ·Ê
	int endline = 500*sizeshort; //°ÔÀÓ»ó ³ëÆ® ³¡ºÎºÐ 
	//int bmsspeed = 6000; //¶³¾îÁö´Â ¼Óµµ
	int bmsspeed = 2000; //¶³¾îÁö´Â ¼Óµµ
	int endlinebms = (endline/bmsspeed-2)*bmsspeed; //¼Óµµ¿¡ µû¸¥ °ÔÀÓ»ó³ëÆ®³¡ºÎºÐÁ¶Á¤
	
	
	
	
	//madisprocess
	int channel = 0;	//Ã¤³ÎÁ¤º¸
	int madiywh= 0;//¸¶µð yÃàÀ§Ä¡
	int textlength= 0 ; //text ¹®ÀÚ ±æÀÌ
	int notesum=1;	//ÇÑ¸¶µð¿¡ ³ëÆ®°³¼ö
	int notelength=0;//³ëÆ® »çÀÌ °£°Ý
	int madilength=400000;//¸¶µð °£°Ý
	int madinum = 0;//¸¶µð ¹øÈ£
	int timelength=0;//ÁøÇàµÈ ±æÀÌ ´©Àû
	String text = "";
	int size ; //³ëÆ®»çÀÌÁî´Â ÇÑ¹ø ÀÐ°í³ª¸é ±×´ë·Î
	Note madi;
	Note note;
	int[] madinotenum; // ¸¶µð ³ëÆ®¿¡ µé¾î°¡ÀÖ´Â¹øÈ£ ÀúÀå
	int prnotenum; // ÇöÀç ³ëÆ®¹øÈ£
	int madipointer = 0;// ÇöÀç Ç¥½ÃÁßÀÎ Ã¹¹øÂ°¸¶µð
	int madipointerlast = 1;// ÇöÀç Ç¥½ÃÁßÀÎ ¸¶Áö¸· ¸¶µð
	int lastpointer;
	boolean autoplay=false; //ÀÚµ¿¸ðµå on, off
	Effect effectbuf;//¸Â­ŸÀ»¶§ ÀÌ¹ÌÁö ÀÓ½ÃÅ¬·¡½º
	ArrayList effectes = new ArrayList();//Ç¥½ÃµÇ´Â ÀÌÆåÆ®È¿°ú °ü¸®
	int lastcheck =0;
	
	//¸Þ¸ð¸® Èü Á¶»ç
	long heapSize;
	String heapSizeMB;
	
	//»ç¿ëµÇ´Â Å°¹øÈ£ °ª
	
	//keyprocess
	int key=0;	// Å° ÀúÀå°ª
	int keybuff = 0; // Å°ÀÌº¥Æ® °ª ÀúÀå
	public final static int S_PRESSED	=0x001;
	public final static int D_PRESSED	=0x002;
	public final static int F_PRESSED	=0x004;
	public final static int SPACE_PRESSED=0x008;
	public final static int J_PRESSED	=0x010;
	public final static int K_PRESSED = 0x020;
	boolean lockkey[] = new boolean[7];
	//crashprocess
	int choicekey[] = new int[7] ; //°¢ Å°¿¡ ´ëÀÀÇÏ´Â ÀÓ½Ã ³ëÆ®¹øÈ£±â¾ï
	Note notecrash;
	
	//game Á¡¼ö °è»ê
	int score;	  //Á¡¼ö
	int combo;		
	int perfect; //¸ÂÀºÈ½¼ö
	int miss;	 //Æ²¸°È½¼ö
	int maxcombo;
	int accurate;//Á¤È®µµ
	
	int prescore=0;//ÀÌÀüµ¥ÀÌÅÍ ÃÖ°íÁ¡¼ö
	
	RythmandBeat(){
		
		add(gamescreen);
		setResizable(false);
		setTitle("Rythm and Beat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLocationRelativeTo(null);
		setVisible(true);
		
		System.out.println(curDir);
		addKeyListener(this);//Å°¸®½º³Ê Ãß°¡
		//mainThread.setPriority(9);
		mainThread.start();//¸ÞÀÎ¾²·¹µå ½ÃÀÛ
		//drawThread.setPriority(4);
		drawThread.start();
		backmusic.play();
	}
	public void run(){
		try {
			while (mainrun) {
				//System.out.println("¾²·¹µå ÀÛµ¿Áß");
				pretime = System.currentTimeMillis();
				process();
				
				
				//¾²·¹µåµ¿ÀÛ ½Ã°£ ÀÏÁ¤ÇÏ°Ô Á¶Àý
				if (System.currentTimeMillis() - pretime < delay){
					Thread.sleep(delay - System.currentTimeMillis() + pretime);
				}
				

			}
		} catch (Exception e) {

		}
	}

	public void process(){ 
		switch(status){
		case 0: //Å¸ÀÌÆ²
			title_process();
			break;
		case 1: //¸Þ´º
			select_process(); 
			break;
		case 2: //À½¾Ç ¼±ÅÃ
			s_music_process();
			break;
		case 3://°ÔÀÓ½ÇÇà ÃÊ±âÈ­ //·Îµù
			init_Playprocess();
			break;
		case 4://°ÔÀÓ½ÇÇà ÁøÇà
			madisprocess();
			keyprocess();
			break;
		case 5://°ÔÀÓ°á°úÈ­¸é
			resultprocess();
			break;
		}
		
		
		//System.out.println("ÇÁ·Î¼¼½º ÀÛµ¿Áß");
	}
	//¸Þ´º ½ÃÀÛ
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
	//¸Þ´º ³¡
	//À½¾Ç ¼±ÅÃ ½ÃÀÛ
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
	//°ÔÀÓ½ÇÇà ½ÃÀÛ
	public void init_Playprocess(){//°ÔÀÓ½ÃÀÛÀü bms ·Îµù
		delay =10;//¾²·¹µå ¼Óµµ º¯°æ
		System.out.println("ÃÊ±â½ÇÇà¼Ò½º ½ÃÀÛ");
		//º¯¼öÃÊ±âÈ­
		 channel = 0;	//Ã¤³ÎÁ¤º¸
		 madiywh= 0;//¸¶µð yÃàÀ§Ä¡
		 textlength= 0 ; //text ¹®ÀÚ ±æÀÌ
		 notesum=1;	//ÇÑ¸¶µð¿¡ ³ëÆ®°³¼ö
		notelength=0;//³ëÆ® »çÀÌ °£°Ý
		 madinum = 0;//¸¶µð ¹øÈ£
		 timelength=0;//ÁøÇàµÈ ±æÀÌ ´©Àû
		 text = "";
		madipointer = 0;// ÇöÀç Ç¥½ÃÁßÀÎ Ã¹¹øÂ°¸¶µð
		madipointerlast = 1;// ÇöÀç Ç¥½ÃÁßÀÎ ¸¶Áö¸· ¸¶µð
		
		notes.clear();
		effectes.clear();
		//game Á¡¼ö °è»ê
		score=0;
		 combo=0;
		perfect=0;
		 maxcombo=0;
		 miss=0;
		 lastcheck=0;//¸¶Áö¸· ¸¶µð °Ë»ç
		lastpointer = madipointerlast;
		gamescreen.playbg=0;
		
		System.out.println(filename);
		br = null;
		br = new BeatReader(filename);
		System.out.println("BeatReader½ÇÇà¿Ï·á");
		
		
		
		bms = br.hbms; // bms °´Ã¼ ´ã¾Æ¿À±â
		madinotenum = new int[bms.lastmadi];//¸¶µð À§Ä¡ ±â¾ï
		prnotenum = 0;
		//bms ³ëÆ® ÀÐ¾î¿À±â
		for (madinum = 0; madinum < bms.lastmadi; madinum++) {
			madinotenum[madinum]= prnotenum;//¸¶µð µé¾î°£¼ø¼­ ±â¾ï
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
				
				//System.out.println("¸¶µð¹øÈ£ : "+madinum+"notesum="+notesum);
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
		
			//madiywh -= madilength; //¸¶µð¸¶´Ù À§Ä¡Á¶Á¤
			
			//System.out.println("ÃÊ±â½ÇÇà¼Ò½º¿Ï·á");
		}
		//º¯¼ö ÃÊ±âÈ­
		for(int i =0 ;i<7 ; i++){
			choicekey[i]=0;
			lockkey[i]= false;
		}
		
		gamescreen.effectes = effectes;
		size = notes.size();// ³ëÆ® °³¼ö ¹Þ¾Æ¿À±â
		System.out.println("¿Ïº®ÃÊ±â½ÇÇà¼Ò½º ¿Ï·á");
		status = 4;//°ÔÀÓ½ÇÇà
		gamescreen.menuchoice = 4;//±×¸®±âÇÁ·Î¼¼½º
		
	}
	void endPlay(){ //´Ù½Ã ´Ù¸¥°î Àç»ý½Ã º¯¼ö ÃÊ±âÈ­ , À½¾Ç°á°úÈ­¸éÀ¸·Î ³Ñ¾î°¨
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
	public void madisprocess(){ //ÇöÀç Ç¥½ÃÁßÀÎ ¸¶µð¸¸ ±×¸®±â
		

		
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
			if(bms.lastmadi-1 == madipointerlast){//³¡³ª¸é °á°úÈ­¸é Ãâ·Â
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
						note.key =80; //»ç¿ëµÇ°íÀÖÁö¾ÊÀºÃ¤³Î
					}else if (note.key == 4) {	
						gamescreen.playbg = note.fnum;
						note.key =80; //»ç¿ëµÇ°íÀÖÁö¾ÊÀºÃ¤³Î
					}else if(autoplay){
						if(note.key>=11 && note.key<=16){
							bms.WAV[note.fnum].play();
							note.key =80; //»ç¿ëµÇ°íÀÖÁö¾ÊÀºÃ¤³Î
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
						note.key =80; //»ç¿ëµÇ°íÀÖÁö¾ÊÀºÃ¤³Î
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
				System.out.println("s´­¸²");
			} 

			break;
		case D_PRESSED:
			
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
				System.out.println("d´­¸²");
			} 	
			break;
		case F_PRESSED:
			
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
				System.out.println("f´­¸²");
			} 	
			break;
		case SPACE_PRESSED:
			
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
				System.out.println("space´­¸²");
			} 	
			break;
		case J_PRESSED:
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
				System.out.println("j´­¸²");
			} 	
			break;	
		case K_PRESSED:
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
				System.out.println("k´­¸²");
			} 	
			break;
			// µÎ°³ Å° Áßº¹
		case S_PRESSED|D_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			System.out.println("s¿Í d´­¸²");break;
		case S_PRESSED|F_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			System.out.println("s¿Í d´­¸²");break;	
		case S_PRESSED|SPACE_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 	
			System.out.println("s¿Í space´­¸²");break;
		case S_PRESSED|J_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("s¿Í j´­¸²");break;
		case S_PRESSED|K_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 	
			System.out.println("s¿Í k´­¸²");break;
		case D_PRESSED|F_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			System.out.println("d¿Í f´­¸²");break;
		case D_PRESSED|SPACE_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 	
			System.out.println("d¿Í space´­¸²");break;
		case D_PRESSED|J_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("d¿Í j´­¸²");break;
		case D_PRESSED|K_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			}
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 	
			System.out.println("d¿Í k´­¸²");break;
		case F_PRESSED|SPACE_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			System.out.println("F¿Í space´­¸²");break;
		case F_PRESSED|J_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("F¿Í j´­¸²");break;
		case F_PRESSED|K_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("F¿Í k´­¸²");break;
		case SPACE_PRESSED|J_PRESSED:
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 
			System.out.println("space¿Í j´­¸²");break;
		case SPACE_PRESSED|K_PRESSED:
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("space¿Í k´­¸²");break;
		case J_PRESSED|K_PRESSED:
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("j¿Í k´­¸²");break;

		
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
	public void resultprocess(){//°á°úÈ­¸é
		if(keybuff == KeyEvent.VK_SPACE|| keybuff==KeyEvent.VK_ENTER||keybuff==KeyEvent.VK_ESCAPE){
			status = 2;
			keybuff=0;
			gamescreen.menuchoice = 2; 
		}
	}
	//keyListener ½ÃÀÛ
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
				System.out.println("esc´­¸²");

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
	//keyListener Á¾·á
	
	class TimerTaskexit extends TimerTask { 
        public void run() { 
        	endPlay();//°ÔÀÓÀ»Á¾·áÇÏ°í °á°úÈ­¸éÀ¸·ÎÀÌµ¿
            timer.cancel(); //timer thread Á¾·á 
        } 
    } 

	
	public static void main(String[] args){
		RythmandBeat t1 = new RythmandBeat();
	}
}

class Note{
	int key;	// Ã¤³Î Á¤º¸
	int y;
	
	int fnum=0;	//	ÇØ´çÇÏ´Â ÆÄÀÏ¹øÈ£(wav,bmp) 
	
	Note(int key , int y){//¸¶µð¸¦ À§ÇÑ »ý¼ºÀÚ
		this.key=key;	//¸¶µð´Â Å°°ªÀÌ 99
		this.y=y;
	}
	Note(int key, int y, String mnum){//ÀÏ¹Ý ³ëÆ®¸¦ À§ÇÑ »ý¼ºÀÚ
		this.key=key;
		this.y=y;
		
		fnum =BeatReader.changehexint(mnum.charAt(0)) * 16;
		fnum +=BeatReader.changehexint(mnum.charAt(1));	
	}
	public void changeY(int y){
		this.y += y;
	}
}
class Effect{//³ëÆ® ¸ÂÃèÀ»¶§ Effect
	int keynum;//Å°¹øÈ£
	int cnt;// 6°³ÀÇ ÀÌ¹ÌÁö(0~5°³)6µÇ¸é Á¾·á
	Effect(int keynum){
		this.keynum=keynum;
		this.cnt = 0;
	}
	
}
class DrawThread extends Thread{
	GameScreen gamescreen;
	long pretime;//¾²·¹µå °£°ÝÀ» Á¶ÀýÇÏ±â À§ÇÑ ½Ã°£ Ã¼Å©°ª
	long delay=33;//¾²·¹µå °£°Ý µô·¹ÀÌ 1000ms=1s,ÇöÀç ÃÊ´ç 30fps
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

