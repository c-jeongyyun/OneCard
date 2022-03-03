package _oneCard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GameReady {

// 변수 선언
	/* 옵션 저장 배열 */
	private ArrayList<Integer> option;
	
	/* 사용자 등록용 이메일 & 패스워드 */
	private String email="";
	private String password="";
	private final char AT_SIGN='@';
	private final String TAB="\t";
	
	/* 전체 사용자 수 & 현재 사용자 수 */
	private int totalPlNum;
	private int curPlNum;
	
	/* 파일 관련 변수 */
	private final String FILE_NAME="GameData.txt";
	private FileReader fReader = null;
	private BufferedReader bReader = null;
	private Scanner sc=new Scanner(System.in);
	
	/* 로그인 관련 변수 */
	private ArrayList<String> loginEmailArr;
	private ArrayList<String> loginPwArr;
	private int loginPlNum;
	
// 생성자
	GameReady(){
		// 옵션 default로 초기화
		this.option= new ArrayList<Integer>(5);
		this.option.add(0,1);
		this.option.add(1,1);
		this.option.add(2,1);
		this.option.add(3,0);
		this.option.add(4,0);
		
		// 총 사용자 수 default로 초기화 
		this.totalPlNum=4;		
	}
	
// 게임 시작 
	/* 게임 시작 메소드 */
	public void start() throws InterruptedException {
		// 첫번째 줄에 데이터가 있는지 
		if(hasData(1))//hasStartNum()
			return;
		int aNum=-1;
		while(aNum==-1) {
			System.out.println(">> 게임을 시작하겠습니까?(Y/N)");
			System.out.print(">");
			String answer = sc.nextLine();
			answer=answer.trim();
			aNum=isYes(answer);
			
			// 잘못된 입력인 경우 
			if(aNum==-1) { 
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.\n");
				
				// 1초 있다가 화면 지우기 
				this.clearConsole();
			}
		}
		if(aNum==0) 
			System.exit(0);
		this.printStartNum();
		this.clearConsole();
		return;
	}
	
	/* 파일에 시작 여부 출력 */
	private void printStartNum() {
		File file=new File(FILE_NAME);
		FileWriter fWriter;
		try {
			fWriter = new FileWriter(file);
			fWriter.write("1");
			fWriter.flush();
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/* 사용자 등록 후 카드, 묘지, 공격 여부 ,순서 초기화 */
	public void initFileAfSignIn() {
		// 카드 배부 및 스택 초기화 
		ShuffleFirstStack(totalPlNum);

	}
	
	/* 파일에 랜덤으로 배부한 카드 출력 - 은찬 */
	/* 파일에 랜덤으로 배부한 카드 출력 - 은찬 */
    public void ShuffleFirstStack(int totalPlNum) { // 은찬 - 처음에 사용자수(totalPlNum)만큼, 시작패(firstHand)만큼 랜덤으로 스택에서 패를 나눈거랑, 남은 스택을 파일에 출력까지 하는 메소드

        // 1인당 초기 패 개수 결정
        int firstHand=0;
        if(totalPlNum==3)
            firstHand =7;
        if(totalPlNum==4)
            firstHand =6;
        if(totalPlNum==5)
            firstHand =5;

        ArrayList<Integer> Stack = new ArrayList<>(54);
        for (int i = 0; i < 54; i++)
            Stack.add(i);

        // ArrayList로 만들어진 스택을 랜덤하게 섞어줌.
        int temp, randomNum1, randomNum2;
        for (int i = 0; i < 54; i++) {
            randomNum1 = (int) (Math.random() * Stack.size());
            randomNum2 = (int) (Math.random() * Stack.size());
            temp = Stack.get(randomNum1);
            Stack.set(randomNum1, Stack.get(randomNum2));
            Stack.set(randomNum2, temp);
        }

        int randomCard = -1;
        File file = new File(FILE_NAME);
        FileWriter fWriter;
        try {

            // 사용자 패에 해당하는것 출력
            for (int i = 0; i < totalPlNum; i++) {
                fWriter = new FileWriter(file, true);
                fWriter.write("\nCardOfPlayer" + i + "\n");
                for (int j = 0; j < firstHand; j++) {
                    randomCard = Stack.remove(0);
                    fWriter.write(randomCard/13 + " " + randomCard%13+ "\n");
                }
                fWriter.write("/");
                fWriter.flush();
                fWriter.close();
            }

            // 남은 스택 출력
            fWriter = new FileWriter(file, true);
            fWriter.write("\nStack\n");
            for (int i = 0; i < 54 - (totalPlNum * firstHand) - 1; i++) { // 뒤에 첫 선행카드 하나 남겨두기위한 -1
                int remainingStack = Stack.remove(0);
                fWriter.write(remainingStack/13 + " " + remainingStack%13 + "\n");
            }
            fWriter.write("/");

            // 묘지 초기화
            fWriter.write("\nGrave\n");
            int firstPrevCard = Stack.remove(0); // 첫 선행카드
            fWriter.write(firstPrevCard/13 + " " +firstPrevCard%13+ "\n/"); // 첫 선행카드 한 장 묘지에 추가 후 "/"로 닫음

            fWriter.write("\nAttack\n/"); // 공격여부 초기화
            fWriter.write("\nTurn\n-1 0 0 0 \n/"); // 차례 초기화
            fWriter.flush();
            fWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	
// 게임 옵션 설정 
	/* 옵션 설정하는 메소드 (메인) */
	public void setOption() throws InterruptedException{
		// 이미 두번째 줄에 옵션 설정되어있는 경우
		if(hasData(2)) {
			// 저장되어있는 옵션 읽어오기 
			readOption();
			return;
		} 
		
		// 옵션 설정 안되어있으면 옵션 선택창 출력
		int menu;
		opt:
		while(true) {
			System.out.println(">> 원하는 옵션의 번호를 입력해주세요.");
			System.out.println("\t1. 게임 규칙 설명");
			System.out.println("\t2. 참여할 사용자 수 설정 (Default: 4명)");
			System.out.println("\t3. 게임 규칙 설정");
			System.out.println("\t4. 게임 시작");
			System.out.print(">_");
			
			try {
				// menu 입력받기
				menu=sc.nextInt();
			}catch (InputMismatchException e){
				// 다른 자료형 입력된 경우 - 재입력 
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				sc.nextLine();
				clearConsole();
				continue;
			}
			
			sc.nextLine(); // 개행문자 처리 
			switch(menu) {
			// 게임설명
			case 1: 
				clearConsole();
				explainRule();
				break;
			// 참여할 사용자 수 입력
			case 2: 
				clearConsole();
				setPlayerNum();
				break;
			// 특수 규칙 설정 
			case 3: 
				clearConsole();
				setSpecialRule();
				break;
			case 4:
				// 화면 지우기 
				this.clearConsole();
				break opt;
			default:
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				clearConsole();
				continue;
			}
		}
		printOptions();
	}
	
	/* 옵션 파일에서 읽어오기 */
	private void readOption() {
		String line = "";
		try{
			// 파일 관련 변수 선언
			File file=new File(FILE_NAME);
			this.fReader = new FileReader(file); 
			this.bReader = new BufferedReader(this.fReader);
			LineNumberReader lineNum = new LineNumberReader(bReader);
			
			// 데이터 파일 읽기 
			while((line = lineNum.readLine())!=null) {
				if(lineNum.getLineNumber()==2) { // 두번째 줄 조회 시 
					String[] option = line.split("\t");
					// option 배열 
					for(int i=0;i<5;i++)
						this.option.set(i,Integer.parseInt(option[i]));
				}
				// 3번째줄 -> while문 탈출 
				if(lineNum.getLineNumber()>2)
					break;
			}
			
			// 파일 읽기 종료 
			lineNum.close(); 
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			System.err.println("파일 읽기 에러");
		}
		finally {
			if(fReader!=null) {
				try {
					bReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	/* 옵션1 - 게임설명 */
	private void explainRule() throws InterruptedException {
		int menu;
		explain:
		while(true) {
			System.out.println(">>원하는 옵션의 번호를 입력해주세요.");
			System.out.println("\t1. 원카드 기본 규칙 설명");
			System.out.println("\t2. 프로그램 내 입력 방식(키 입력 방식)");
			System.out.println("\t3. 뒤로 가기");
			System.out.print("\n>_");
			try {
				// menu 입력받기
				menu=sc.nextInt();
			}catch (InputMismatchException e){
				// 다른 자료형 입력된 경우 
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				sc.nextLine();
				continue;
			}
			//String back;
			switch(menu) {
			case 1:
				clearConsole();
				System.out.println("기본 규칙) 선행카드와 동일한 숫자 또는 무늬의 카드를 계속해서 제시하며 자신의 모든 패를 소모하면 승리. "
						+ "\n예를 들어 선행카드가 ♥5라면 ♥ 모양의 카드나 숫자가 5인 카드만 제시할 수 있습니다."
						+ "\n만약 자기의 패 중에서 제시할 수 있는 카드가 없다면, 카드 스택에서 카드 한 장을 자신의 패로 가져가고 파일을 종료하여 다음 사람에게 순서를 넘겨야 합니다. "
						+ "\n승자와 파산자가 한 명씩 게임에서 이탈해 모든 사용자가 패가 0이 되면 최종 등수를 매기며 게임이 종료됩니다. ");
				System.out.println("\n>> 뒤로 가려면 Back키를 입력해주세요. (Back, B, b) ");
				//System.out.print(">_");
				//back=sc.nextLine();
				sc.nextLine();
				inputBack();
				clearConsole();
				break;
			case 2:
				clearConsole();
				System.out.println("설명 1) 사용자는 카드를 제시할 때 숫자와 문자를 순서에 관계없이 입력하게 되는데, "
						+ "\n이 때 숫자와 문자 사이에는 1) 공백, 2) tab키, 3) /, 4)  _  4가지 중 하나를 입력하여 구분을 주어야 합니다. \r\n"
						+ "\r\n"
						+ "\t● h, H로 시작하면 ♥로 인식 /  s, S로 시작하면 ♠ 로 인식 / c, C로 시작하면 ♣ 로 인식 / d, D 로 시작하면  ♦ 로 인식합니다. "
						+ "\n\t예를 들어 ♥2 카드를 제시하고 싶으면 \r\n"
						+ "\n\t\t[2 H]  [2 tab H] [2/H] [2_H]\r\n"
						+ "\t\t[h 2] [h tab 2] [h/2] [h_2]\r\n"
						+ "\t\t[heart_2] [Hart 2] [hat 2]\r\n"
						+ "\n\t등과 같이 입력해 주어야 합니다.\r\n"
						+ "\n\t● 조커는 @ 로 입력합니다. 예를 들어 , 컬러 조커는  [c @] [C_@] [@ color] [@ cala] 등과 같이 입력하며, "
						+ "\n\t@ 키와 함께 입력된 c는 클로버가 아닌 color입니다.흑백 조커는  [b @] [B_@] [black @] 등과 같이 입력합니다.\r\n"
						+ "\r\n"
						+ "설명 2) 사용자가 자신의 차례에 낼 수 있는 카드가 없거나 내고 싶지 않은 경우 카드를 먹게 되는데, "
						+ "\n이 때에는  End,  E,  e 중 하나를 입력해야 합니다.\r\n"
						+ "");
				System.out.println("\n>> 뒤로 가려면 Back키를 입력해주세요. (Back, B, b) ");
				sc.nextLine();
				inputBack();
				clearConsole();
				break;
			case 3: 
				this.clearConsole();
				break explain;
			default:
				System.out.println("잘못된 입력입니다. 다시 입력해주세요");
				this.clearConsole();
				continue;
			}
		}
	}
	
	/* 옵션2 - 사용자 수 설정 */
	private void setPlayerNum() throws InterruptedException{
		// 입력한 인원 수 
		int num;
		while(true) {
			System.out.println(">> 게임에 참여할 사용자 수를 입력해 주세요(범위: 3명 ~ 5명) (ex. 3)");
			String plNum=sc.nextLine();
			
			// 범위내의 인원수 입력했다면 인원 수 변수에 저장 
			if(plNum.equals("3")||plNum.equals("4")||plNum.equals("5")||plNum.equals("3명")||plNum.equals("4명")||plNum.equals("5명")) 
				num = plNum.charAt(0) - '0';
			else {
				System.out.println("잘못된 입력입니다. 3에서 5사이의 숫자를 입력해주세요.");
				clearConsole();
				continue;
			}
			this.totalPlNum=num;
			break;
		}
		
	}
	
	/* 옵션3 - 특수 규칙 설정 */
	private void setSpecialRule() throws InterruptedException{
		int menu;
		opt:
		while(true) {
			System.out.println(">> 추가할 규칙을 선택하세요.");
			System.out.println("\t1. J, Q, K 룰 설정(기본값: ON)");
			System.out.println("\t2. 컬러조커 특수 능력(기본값: OFF)");
			System.out.println("\t3. 5 특수 카드 설정 (기본값): OFF");
			System.out.println("\t4. 뒤로 가기");
			System.out.print(">_");
			
			try {
				// menu 입력받기
				menu=sc.nextInt();
			}catch (InputMismatchException e){
				// 다른 자료형 입력된 경우 
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				sc.nextLine();
				clearConsole();
				continue;
			}
			
			sc.nextLine();
			switch(menu) {
			case 1:
				setJQK();
				clearConsole();
				break;
			case 2:
				setJokerRule();
				clearConsole();
				break;
			case 3: 
				setSpecial5();
				clearConsole();
				break;
			case 4:
				// 화면 지우기 
				this.clearConsole();
				break opt;
			default:
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				clearConsole();
				continue;
			}
		}
		
	}
	
	/* J,Q,K 규칙 설정 */
	private void setJQK() throws InterruptedException{
		String answer;
		System.out.println(">> J: 다음 사용자의 차례를 넘깁니다. (Y/N)");
		
		while(true) {
			System.out.print(">_");
			answer = sc.nextLine();
			if(isYes(answer)==1) { 
				this.option.set(0, 1);
				break;
			}else if(isYes(answer)==0){
				this.option.set(0, 0);
				break;
			}else {
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				continue;
			}
		}
		System.out.println(">> Q: 사용자의 차례가 다음 사용자부터 거꾸로 됩니다. (Y/N)");
		while(true) {
			System.out.print(">_");
			answer = sc.nextLine();
			if(isYes(answer)==1) { 
				this.option.set(1, 1);
				break;
			}else if(isYes(answer)==0){
				this.option.set(1, 0);
				break;
			}else {
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				continue;
			}
		}
		
		System.out.println(">> K: 사용자가 K를 낸 이후에 문양이 같은 숫자 카드를 하나 더 낼 수 있습니다. (Y/N)");
		while(true) {
			System.out.print(">_");
			answer = sc.nextLine();
			if(isYes(answer)==1) { 
				this.option.set(2, 1);
				break;
			}else if(isYes(answer)==0){
				this.option.set(2, 0);
				break;
			}else {
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				continue;
			}
		}
		clearConsole();
		
		
	}

	/* 컬러 조커 특수 규칙 설정 */
	private void setJokerRule(){
		System.out.println("컬러 조커 특수 능력)\n");
		System.out.println(">> 컬러 조커가 7장의 카드로 공격하는 것 이외에 옵션 선택 후 특정사용자의 번호를 입력할 시에, 그 사용자와 카드가 뒤바뀌는 기능을 추가합니다. (Y/N)");
		while(true) {
			String answer;
			System.out.print(">_");
			answer=sc.nextLine();
			if(isYes(answer)==1) { 
				this.option.set(3, 1);
				break;
			}else if(isYes(answer)==0){
				this.option.set(3, 0);
				break;
			}else {
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				continue;
			}
		}
	}
	
	/* 숫자카드 5 특수 규칙 설정 */
	private void setSpecial5() {
		
		System.out.println(">> 숫자 카드 5가 더 이상 낼 수 없는 상황일 때 낼 수 있게 됩니다. 다음 사용자는 숫자 카드 5의 문양을 따라갑니다. (Y/N)");
		while(true) {
			String answer;
			System.out.print(">_");
			answer=sc.nextLine();
			if(isYes(answer)==1) { 
				this.option.set(4, 1);
				break;
			}else if(isYes(answer)==0){
				this.option.set(4, 0);
				break;
			}else {
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				continue;
			}
		}
	}
	
	
	/* 설정한 옵션 파일에 출력하는 메소드 */
	private void printOptions() { 
		File file=new File(FILE_NAME);
		
		// option 배열의 설정값 -> 하나의 string 으로 받아오기
		String opt="";
		for(int i=0;i<5;i++) {
			opt += (this.option.get(i) + TAB);
		}
		
		// 파일 뒤에 옵션 내용 이어쓰기 
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
			PrintWriter pWriter= new PrintWriter(bw,true);
			
			// 옵션 String 저장
			pWriter.write("\n"+opt);
			pWriter.flush();
			pWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/* 옵션 반환하는 메소드 */
	public ArrayList<Integer> getOption(){return this.option;}

// 사용자수 읽어오기 
	/* 총 사용자수 & 현재 등록 사용자 수 가져오는 메소드 - 정윤 */
 	private void readPlNum() {
		String line = "";
		try{
			// 파일 관련 변수 선언
			File file=new File(FILE_NAME);
			this.fReader = new FileReader(file); 
			this.bReader = new BufferedReader(this.fReader);
			LineNumberReader lineNum = new LineNumberReader(bReader);
			
			// 데이터 파일 읽기 
			while((line = lineNum.readLine())!=null) {
				if(lineNum.getLineNumber()==3) { // 세번째 줄 조회 시 
					String[] playerNum = line.split("\t");
					
					// total 인원 & 현재 인원 
					totalPlNum = Integer.parseInt(playerNum[0]);
					curPlNum = Integer.parseInt(playerNum[1]);
				}
			}
			lineNum.close(); // 파일 읽기 종료 
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			System.err.println("파일 읽기 에러");
		}
		finally {
			if(fReader!=null) {
				try {
					bReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
// 사용자 등록
	/* 사용자 등록 메소드 - 정윤 */
	
 // 사용자 등록
 	public int signIn() throws InterruptedException {
		this.readPlNum();
		if(this.curPlNum >= this.totalPlNum)
			return 0;
		System.out.println("--- 현재 등록 인원 : "+this.curPlNum +'/'+this.totalPlNum+"---");
		
		System.out.println(">> 사용자를 등록해주세요.");
		inputEmail();
		inputPw();
		System.out.println("등록이 완료되었습니다. ");
		this.curPlNum++;
		
		// 변경된 내용 파일에 출력 
		renewCurPlNum();
		Thread.sleep(5000);
		// 파일 아예 종료하는 메소드
		System.exit(0);
		return 0;
	}
	
// 총 사용자 수 반환 

 // totalPlNum 반환
 	public int getTotalNum(){return this.totalPlNum;};
	
 	
 	
 	
// 이메일
	/* 이메일 입력 */
	/* 이메일 입력 메소드 - 정윤 */
	private int inputEmail() {
		while(true) {
			System.out.print("이메일: ");// 사용자에게 입력 받기 -> this.email 로 받기 
															//----------------------------------원래 탭이나 스페이스바쳐도 넘어가야 하는데 구현 방법을 모르겠음.
			String inputEmail=sc.nextLine();
			
			// 올바른 이메일인지 확인 
			if(isRightEmail(inputEmail)==false) {
				System.out.println("잘못된 이메일 입력입니다. 다시 입력해주세요.");// 틀린 형식의 이메일일 시 재입력 
				continue;
			}
			
			this.email=this.fixEmail(inputEmail); // 이메일 완전한 형태로 변경 
			
			// 중복 이메일 확인 - 중복일 시 재입력
			if(curPlNum>0) {
				if(isOverLapEmail(this.email)==true) {   
					System.out.println("중복되는 이메일이 존재합니다. 다시 입력해주세요");
					continue;
				}
			}
			
			// 정확한 이메일 - 종료 			 
			break;
		}
		return 0;
	}
	
	/* 이메일 판단 메소드 - 정윤 */ // test 하려면 public으로
	
	/* 올바른 이메일 형식인지 판단 */
	private boolean isRightEmail(String email) {
		int indexAt=email.indexOf(AT_SIGN);  // @가 있는 인덱스 찾기
		
		// @ 있는지
		if (indexAt<0) 
			return false;
		
		// @로 끝나고 뒤에 사이트 명이 없는지
		if(indexAt==email.length()-1)
			return false;
		
		// @앞에 특수문자있는지
		for(int i=0; i<indexAt;i++) { //@ 이후 한문자씩 조회
			char ch= email.charAt(i); 
			if( ch<48 ||(ch>57 && ch<64)||(ch>90 && ch<97) || ch>122) { // 특수문자 있는 경우 false; 
				return false;
			}
		}
		
		// @뒤에 n 또는 g 또는 k로 시작하는지 
		char nextAt=email.charAt(indexAt+1); 
		if(nextAt=='n'||nextAt=='g'||nextAt=='k')
			return true; // n, g, k면 true
		 
		return false; // 아니면 false;
		
	}
	
	/* @뒷부분 완전하게 수정하는 메소드 - 정윤*/
	
	/* 이메일 도메인명 수정 */
	private String fixEmail(String email) { // test 하려면 public으로 
		
		int nextAt=email.indexOf(AT_SIGN)+1;
		char nextCh=email.charAt(nextAt);
		String site="";
		String id=email.substring(0, nextAt); // id@ 부분만 가져오기
		
		switch(nextCh) {
		case 'n':
			site="naver.com";
			break;
		case 'g':
			site="gmail.com";
			break;
		case 'k':
			site="konkuk.ac.kr";
			break;		
		}
		return id+site; // 완전한 이메일 형식 반환
	}

	/* 이메일 중복 여부 판단 메소드 - 정윤 */
	
	/* 이메일 중복 검사 메소드 - 정윤 */
	
	/* 중복 이메일인지 판단 */
	private boolean isOverLapEmail(String email) {
		try{
			
			// 파일 읽기용 변수 선언
			String line = "";		
			File file=new File(FILE_NAME);
			this.fReader = new FileReader(file); 
			this.bReader = new BufferedReader(this.fReader);
			LineNumberReader lineNum = new LineNumberReader(bReader);
			int i=0;
			
			// 데이터 파일 읽기 
			while((line = lineNum.readLine())!=null && i<this.curPlNum) {
				if(lineNum.getLineNumber()==4+i) { // 4번째 줄부터 email 가져오기
					String[] othersInfo = line.split("\t");
					
					// 중복 여부 검사 
					if(email.equals(othersInfo[0])) 
						return true; // 중복 이메일 있다면 true 반환 
					i++;			
				}
				
			}
			return false; // 중복없다면 false 반환
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
// 비밀번호 
	/* 비밀번호 입력 메소드 - 정윤 */
	private int inputPw(){
		while(true) {
			System.out.print("비밀번호: ");
			
			String pw=sc.nextLine();
			
			if (!isRightPw(pw)) {
				System.out.println("잘못된 비밀번호 입력입니다. 다시 입력해주세요.");
				continue;				
			}
			this.password=pw;
			sc.close();
			break;
		}
		return 0;
	}
	
	/* 비밀번호 형식 판단 메소드 - 정윤 */


// 비밀번호
	/* 올바른 비밀번호 형식인지 판단 */
	private boolean isRightPw(String pw) { // test 시 public으로 
		int len=pw.length();
		
		// 비밀번호 길이 판단
		if(len<8||len>15) {
			return false;
		}
		
		// 비밀번호 특수문자 유무 판단 
		for(int i=0; i<len;i++) { // pw 한글자씩 조회
			char ch= pw.charAt(i); 
			if( ch<48 ||(ch>57 && ch<65)||(ch>90 && ch<97) || ch>122) { // 특수문자 있는 경우 false; 
				return false;
			}
		}
		return true;
		
	}
	
	
	
	
// 변경 내용 출력 
	
	
// 파일 변경내용 출력
	/* 사용자 등록 이후 파일에 변경된 내용 저장하는 메소드 - 정윤 */
	private void renewCurPlNum() {
		String dummy1=""; 
		String dummy2="";	
		String line = "";
		
		try{
			File file=new File(FILE_NAME);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferReader = new BufferedReader(fileReader);
			
			// 삭제할 line전까지 읽어서 가져오기
			if(file.length()!=0){
				for(int i=0;i<2;i++) { 
					line=bufferReader.readLine();
					dummy1 += line+"\n";
				}
				// 삭제할 line
				String delData=bufferReader.readLine();

				while(line!=null) {
					line=bufferReader.readLine();
					if(line!=null) {
						dummy2 += line+"\n";
					}
						
				}
			}
			bufferReader.close();
			
			// 앞 내용 덮어쓰기 
			FileWriter fWriter=new FileWriter(file);
			fWriter.write(dummy1);
			fWriter.flush();
			fWriter.close();
			
			// 변경된 내용부터 이어쓰기 
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
			PrintWriter pWriter= new PrintWriter(bw,true);
			
			// curPlNum  출력 
			pWriter.write(totalPlNum + "\t" + curPlNum+'\n');
			
			// 뒷 내용 + 추가된 이메일 출력 
			pWriter.write(dummy2);
			pWriter.write(this.email + TAB + this.password);
			pWriter.flush();
			pWriter.close();
			
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			System.err.println("파일 읽기 에러");
		}
		

	}
	
// 로그인 
	/* 로그인 메소드 - 정윤 */
	
// 로그인
	public void logIn() throws InterruptedException{
		System.out.println(">> 사용자 정보를 입력해주세요.");
		while(true) {
			System.out.print("이메일: ");
			String logInEmail=sc.nextLine(); // 입력형식 맞는지도 확인해야함.
			System.out.print("비밀번호: ");
			String logInPw=sc.nextLine();
			
			// 이메일 형식에 맞는 경우 @ 뒤 도메인명 정정 
			if(this.isRightEmail(logInEmail)) {
				logInEmail=this.fixEmail(logInEmail);
			}
			
			// 파일에서 이메일 비밀번호 가져오기 
			setEpwArrFromFile();
			
			// 이메일, 비밀번호 인덱스 가져오기 
			int emailI=loginEmailArr.indexOf(logInEmail);
			
			if(emailI>=0){// 이메일 존재하는 경우 
				// 비밀번호 맞는지 체크 
				if(loginPwArr.get(emailI).equals(logInPw)) {
					this.loginPlNum=emailI; // 맞으면 break
					break;
				}
				// 비밀번호 맞지 않는 경우 
				cantLogin();
			}
			
			// 존재하지 않는 이메일인 경우  
			if(emailI<0) {
				cantLogin();
				continue;
			}
			
		}
	}
	private void cantLogin() throws InterruptedException {
		clearConsole();
		System.out.println("\n\n--- 잘못된 정보를 입력했습니다.---");
		System.out.println("이메일: 영문과 숫자 조합 문자열@도메인 주소");
		System.out.println("비밀번호: 8자 이상 15자 이하 영문과 숫자 조합 문자열\n\n");
		System.out.println(">> 대소문자는 구분이 됩니다. 사용자 정보를 다시 입력해주세요.");
	}
	
	/* 로그인 시 동치 비교할 이메일, 비밀번호 ArrayList 만들기 */
	private void setEpwArrFromFile() {
		// 이메일과 비밀번호 담는 배열 초기화 
		this.loginEmailArr=new ArrayList<String>(this.totalPlNum);
		this.loginPwArr=new ArrayList<String>(this.totalPlNum);
		
		try{
			
			// 파일 읽기용 변수 선언
			String line = "";		
			File file=new File(FILE_NAME);
			this.fReader = new FileReader(file); 
			this.bReader = new BufferedReader(this.fReader);
			LineNumberReader lineNum = new LineNumberReader(bReader);
			int i=0;
			
			// 데이터 파일 읽기 
			while((line = lineNum.readLine())!=null && i<this.totalPlNum) {
				if(lineNum.getLineNumber()==4+i) { // 4번째 줄부터 email 가져오기
					String[] playerInfo = line.split("\t");
					
					loginEmailArr.add(i,playerInfo[0]);
					loginPwArr.add(i, playerInfo[1]);
					i++;			
				}
				
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

// 기본 사용 메소드 
	/* 현재 로그인한 사용자 번호 반환 */
	public int getLoginPlNum() {return this.loginPlNum;}
	
	/* CardOfPlayer 또는 카드가 있는지 판단하는 메소드 */
	public boolean hasCard() {
		return hasData(4+this.totalPlNum);
	}
	

// 기본 메소드 
	/* 화면 지우는 메소드 */
	 private void clearConsole() throws InterruptedException {
		Thread.sleep(500);	
    }
		

	 /* Y/N 입력에 따른 정수 반환 */
	private int isYes(String a) {
		if(a.equals("Yes")||a.equals("yes")||a.equals("Y")||a.equals("y")||a.equals("예")||a.equals("ㅇ")) 
			return 1;
		if(a.equals("No")||a.equals("no")||a.equals("N")||a.equals("n")||a.equals("아니오")||a.equals("ㄴ"))
			return 0;
		return -1;
	}
	
	/* Back 입력에 따른 반복수행 */
	private boolean inputBack() throws InterruptedException{
		while(true) {
			System.out.print(">_");
			String back=sc.nextLine();
			if(back.equals("Back")||back.equals("B")||back.equals("b")) 
				return true;
			else {
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				this.clearConsole();
			}
		}
	}

	/* n번째 줄에 데이터 있는지 판단하는 함수 */
	private boolean hasData(int n) {
		String line = "";
		try{
			// 파일 관련 변수 선언
			File file=new File(FILE_NAME);
			this.fReader = new FileReader(file); 
			this.bReader = new BufferedReader(this.fReader);
			LineNumberReader lineNum = new LineNumberReader(bReader);
			
			int count=0;
			// 데이터 파일 읽기 
			while((line = lineNum.readLine())!=null) {
				count++;			
			}
			
			// n번째 줄에 데이터 있는 경우
			if(count> n-1) 
				return true;
			
			// n번째 줄에 데이터 없는 경우 
			lineNum.close(); // 파일 읽기 종료 
			return false;
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			System.err.println("파일 읽기 에러");
		}
		finally {
			if(fReader!=null) {
				try {
					bReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
			
}
