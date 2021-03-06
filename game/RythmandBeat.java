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


//substring(0,1)은 문자열중 0번째 한글자만출력. 뒤 매개변수앞까지 만 표시 
//문자열은 항상 == 아니라 indexof로 비교

public  class RythmandBeat extends JFrame  implements
Runnable, KeyListener{
	long pretime;//쓰레드실행 이전시간 기억
	//long delay=33;//쓰레드 간격 딜레이 1000ms=1s,현재 초당 30fps
	long delay=50;//쓰레드 간격 딜레이 1000ms=1s,17/1000초 = 58 (프레임/초)
	long fpscheck = 0; //초당 fps 체크
	
	Timer timer;
	
	String curDir = System.getProperty("user.dir"); 
	
	boolean mainrun=true;//메인 쓰레드 작동 유무
	Thread mainThread = new Thread(this);//메인 쓰레드 생성
	
	GameScreen gamescreen = new GameScreen(this);//게임화면그리기객체생성
	DrawThread drawThread = new DrawThread(gamescreen);//화면쓰레드생성
	
	GameSound backmusic = new GameSound(getClass().getResource("bg/PororoED.wav"));
	
	int status =0;	//진행프로세스
	
	//s_music_process()
	BMSCheck bmsselect = new BMSCheck();
	String filename = null;
	 //bms 부분
	 BeatReader br;//bms 리더기
	 BMS bms ;// br로부터 bms 담을 객체
	ArrayList notes = new ArrayList();//표시되는 노트 관리
	
	
	int sizeshort = 1000; // 1/1000;실제 자바표시위치와 게임상 거리 비례
	int endline = 500*sizeshort; //게임상 노트 끝부분 
	//int bmsspeed = 6000; //떨어지는 속도
	int bmsspeed = 2000; //떨어지는 속도
	int endlinebms = (endline/bmsspeed-2)*bmsspeed; //속도에 따른 게임상노트끝부분조정
	
	
	
	
	//madisprocess
	int channel = 0;	//채널정보
	int madiywh= 0;//마디 y축위치
	int textlength= 0 ; //text 문자 길이
	int notesum=1;	//한마디에 노트개수
	int notelength=0;//노트 사이 간격
	int madilength=400000;//마디 간격
	int madinum = 0;//마디 번호
	int timelength=0;//진행된 길이 누적
	String text = "";
	int size ; //노트사이즈는 한번 읽고나면 그대로
	Note madi;
	Note note;
	int[] madinotenum; // 마디 노트에 들어가있는번호 저장
	int prnotenum; // 현재 노트번호
	int madipointer = 0;// 현재 표시중인 첫번째마디
	int madipointerlast = 1;// 현재 표시중인 마지막 마디
	int lastpointer;
	boolean autoplay=false; //자동모드 on, off
	Effect effectbuf;//맞췃을때 이미지 임시클래스
	ArrayList effectes = new ArrayList();//표시되는 이펙트효과 관리
	int lastcheck =0;
	
	//메모리 힙 조사
	long heapSize;
	String heapSizeMB;
	
	//사용되는 키번호 값
	
	//keyprocess
	int key=0;	// 키 저장값
	int keybuff = 0; // 키이벤트 값 저장
	public final static int S_PRESSED	=0x001;
	public final static int D_PRESSED	=0x002;
	public final static int F_PRESSED	=0x004;
	public final static int SPACE_PRESSED=0x008;
	public final static int J_PRESSED	=0x010;
	public final static int K_PRESSED = 0x020;
	boolean lockkey[] = new boolean[7];
	//crashprocess
	int choicekey[] = new int[7] ; //각 키에 대응하는 임시 노트번호기억
	Note notecrash;
	
	//game 점수 계산
	int score;	  //점수
	int combo;		
	int perfect; //맞은횟수
	int miss;	 //틀린횟수
	int maxcombo;
	int accurate;//정확도
	
	int prescore=0;//이전데이터 최고점수
	
	RythmandBeat(){
		
		add(gamescreen);
		setResizable(false);
		setTitle("Rythm and Beat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLocationRelativeTo(null);
		setVisible(true);
		
		System.out.println(curDir);
		addKeyListener(this);//키리스너 추가
		//mainThread.setPriority(9);
		mainThread.start();//메인쓰레드 시작
		//drawThread.setPriority(4);
		drawThread.start();
		backmusic.play();
	}
	public void run(){
		try {
			while (mainrun) {
				//System.out.println("쓰레드 작동중");
				pretime = System.currentTimeMillis();
				process();
				
				
				//쓰레드동작 시간 일정하게 조절
				if (System.currentTimeMillis() - pretime < delay){
					Thread.sleep(delay - System.currentTimeMillis() + pretime);
				}
				

			}
		} catch (Exception e) {

		}
	}

	public void process(){ 
		switch(status){
		case 0: //타이틀
			title_process();
			break;
		case 1: //메뉴
			select_process(); 
			break;
		case 2: //음악 선택
			s_music_process();
			break;
		case 3://게임실행 초기화 //로딩
			init_Playprocess();
			break;
		case 4://게임실행 진행
			madisprocess();
			keyprocess();
			break;
		case 5://게임결과화면
			resultprocess();
			break;
		}
		
		
		//System.out.println("프로세스 작동중");
	}
	//메뉴 시작
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
	//메뉴 끝
	//음악 선택 시작
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
	//게임실행 시작
	public void init_Playprocess(){//게임시작전 bms 로딩
		delay =10;//쓰레드 속도 변경
		System.out.println("초기실행소스 시작");
		//변수초기화
		 channel = 0;	//채널정보
		 madiywh= 0;//마디 y축위치
		 textlength= 0 ; //text 문자 길이
		 notesum=1;	//한마디에 노트개수
		notelength=0;//노트 사이 간격
		 madinum = 0;//마디 번호
		 timelength=0;//진행된 길이 누적
		 text = "";
		madipointer = 0;// 현재 표시중인 첫번째마디
		madipointerlast = 1;// 현재 표시중인 마지막 마디
		
		notes.clear();
		effectes.clear();
		//game 점수 계산
		score=0;
		 combo=0;
		perfect=0;
		 maxcombo=0;
		 miss=0;
		 lastcheck=0;//마지막 마디 검사
		lastpointer = madipointerlast;
		gamescreen.playbg=0;
		
		System.out.println(filename);
		br = null;
		br = new BeatReader(filename);
		System.out.println("BeatReader실행완료");
		
		
		
		bms = br.hbms; // bms 객체 담아오기
		madinotenum = new int[bms.lastmadi];//마디 위치 기억
		prnotenum = 0;
		//bms 노트 읽어오기
		for (madinum = 0; madinum < bms.lastmadi; madinum++) {
			madinotenum[madinum]= prnotenum;//마디 들어간순서 기억
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
				
				//System.out.println("마디번호 : "+madinum+"notesum="+notesum);
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
		
			//madiywh -= madilength; //마디마다 위치조정
			
			//System.out.println("초기실행소스완료");
		}
		//변수 초기화
		for(int i =0 ;i<7 ; i++){
			choicekey[i]=0;
			lockkey[i]= false;
		}
		
		gamescreen.effectes = effectes;
		size = notes.size();// 노트 개수 받아오기
		System.out.println("완벽초기실행소스 완료");
		status = 4;//게임실행
		gamescreen.menuchoice = 4;//그리기프로세스
		
	}
	void endPlay(){ //다시 다른곡 재생시 변수 초기화 , 음악결과화면으로 넘어감
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
	public void madisprocess(){ //현재 표시중인 마디만 그리기
		

		
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
			if(bms.lastmadi-1 == madipointerlast){//끝나면 결과화면 출력
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
						note.key =80; //사용되고있지않은채널
					}else if (note.key == 4) {	
						gamescreen.playbg = note.fnum;
						note.key =80; //사용되고있지않은채널
					}else if(autoplay){
						if(note.key>=11 && note.key<=16){
							bms.WAV[note.fnum].play();
							note.key =80; //사용되고있지않은채널
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
						note.key =80; //사용되고있지않은채널
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
				System.out.println("s눌림");
			} 

			break;
		case D_PRESSED:
			
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
				System.out.println("d눌림");
			} 	
			break;
		case F_PRESSED:
			
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
				System.out.println("f눌림");
			} 	
			break;
		case SPACE_PRESSED:
			
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
				System.out.println("space눌림");
			} 	
			break;
		case J_PRESSED:
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
				System.out.println("j눌림");
			} 	
			break;	
		case K_PRESSED:
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
				System.out.println("k눌림");
			} 	
			break;
			// 두개 키 중복
		case S_PRESSED|D_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			System.out.println("s와 d눌림");break;
		case S_PRESSED|F_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			System.out.println("s와 d눌림");break;	
		case S_PRESSED|SPACE_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 	
			System.out.println("s와 space눌림");break;
		case S_PRESSED|J_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("s와 j눌림");break;
		case S_PRESSED|K_PRESSED:
			if (lockkey[6] == false) {
				crashprocess(S_PRESSED);
				lockkey[6] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 	
			System.out.println("s와 k눌림");break;
		case D_PRESSED|F_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			System.out.println("d와 f눌림");break;
		case D_PRESSED|SPACE_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 	
			System.out.println("d와 space눌림");break;
		case D_PRESSED|J_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			} 	
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("d와 j눌림");break;
		case D_PRESSED|K_PRESSED:
			if (lockkey[1] == false) {
				crashprocess(D_PRESSED);
				lockkey[1] = true;
			}
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 	
			System.out.println("d와 k눌림");break;
		case F_PRESSED|SPACE_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			System.out.println("F와 space눌림");break;
		case F_PRESSED|J_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 	
			System.out.println("F와 j눌림");break;
		case F_PRESSED|K_PRESSED:
			if (lockkey[2] == false) {
				crashprocess(F_PRESSED);
				lockkey[2] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("F와 k눌림");break;
		case SPACE_PRESSED|J_PRESSED:
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 
			System.out.println("space와 j눌림");break;
		case SPACE_PRESSED|K_PRESSED:
			if (lockkey[3] == false) {
				crashprocess(SPACE_PRESSED);
				lockkey[3] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("space와 k눌림");break;
		case J_PRESSED|K_PRESSED:
			if (lockkey[4] == false) {
				crashprocess(J_PRESSED);
				lockkey[4] = true;
			} 
			if (lockkey[5] == false) {
				crashprocess(K_PRESSED);
				lockkey[5] = true;
			} 
			System.out.println("j와 k눌림");break;

		
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
	public void resultprocess(){//결과화면
		if(keybuff == KeyEvent.VK_SPACE|| keybuff==KeyEvent.VK_ENTER||keybuff==KeyEvent.VK_ESCAPE){
			status = 2;
			keybuff=0;
			gamescreen.menuchoice = 2; 
		}
	}
	//keyListener 시작
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
				System.out.println("esc눌림");

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
	//keyListener 종료
	
	class TimerTaskexit extends TimerTask { 
        public void run() { 
        	endPlay();//게임을종료하고 결과화면으로이동
            timer.cancel(); //timer thread 종료 
        } 
    } 

	
	public static void main(String[] args){
		RythmandBeat t1 = new RythmandBeat();
	}
}

class Note{
	int key;	// 채널 정보
	int y;
	
	int fnum=0;	//	해당하는 파일번호(wav,bmp) 
	
	Note(int key , int y){//마디를 위한 생성자
		this.key=key;	//마디는 키값이 99
		this.y=y;
	}
	Note(int key, int y, String mnum){//일반 노트를 위한 생성자
		this.key=key;
		this.y=y;
		
		fnum =BeatReader.changehexint(mnum.charAt(0)) * 16;
		fnum +=BeatReader.changehexint(mnum.charAt(1));	
	}
	public void changeY(int y){
		this.y += y;
	}
}
class Effect{//노트 맞췄을때 Effect
	int keynum;//키번호
	int cnt;// 6개의 이미지(0~5개)6되면 종료
	Effect(int keynum){
		this.keynum=keynum;
		this.cnt = 0;
	}
	
}
class DrawThread extends Thread{
	GameScreen gamescreen;
	long pretime;//쓰레드 간격을 조절하기 위한 시간 체크값
	long delay=33;//쓰레드 간격 딜레이 1000ms=1s,현재 초당 30fps
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

