package game;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

public class BMS {
	File file;		//�����̸�
	int PLAYER;	   // 1�ο� 2�ο�
	String GENRE; //�帣
	String TITLE;
	String ARTIST;
	double BPM; //Beats Per Minute. default :130
	int PLAYLEVEL;
	int RANK ; 	// 0: very hard, 1: hard, 2: normal, 3: easy . default : 2
	double VOLWAV ; // volume control (percentage)
	double TOTAL; //increments of Groove Gauge ex) TOTAL=120 ->120%
	Image BMP[]= new Image[256]; //FF����
	GameSound WAV[]= new GameSound[256];//FF����
	//madi, channel �Ѽ�Ʈ ����
	ArrayList[] madi;	//���� ���� ���� �ȿ��� Ư������ ���� ä���� �� ����ִ�.
	ArrayList[] channel;//ä������ ���� �ȿ��� Ư������ ���� �Ǻ� ������ ����ִ�.
	
	int lastmadi;
	
	public BMS(){
		
	}
	

	
}
