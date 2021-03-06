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

public class Player {
	String email; // 사용자 이메일
	String password; // 사용자 비밀번호
	
	
	int cardNum ; //보유한 카드 수
	
	//파일 관련 변수
	FileReader fileReader = null;
	BufferedReader bufferReader = null;
	String line = null;
	final String FILE_NAME="GameData.txt"; //--- 변수 추가 (파일명 상수로 선언)
	
	//사용자별 보유패 저장되어 있는 곳
	ArrayList<Card> pCard = new ArrayList<>();
	
	
	public Player() {
		
	}
	
// 생성자  
	public Player(int plNum) { //--- 변수 추가: plNum: 사용자 번호 // 수정 전: (String email, String password,int plNum) 

		this.readEmail(plNum); // -- email 초기화 
		this.readPlCard(plNum); //-- pCard 초기화
	}
	
	public ArrayList<Card> getPCard(){ //--- 메소드 추가 
		return this.pCard;
	}
	
	public String getEmail() {return this.email;}
	
	public void readEmail(int plNum) { 
		try {
			File file = new File(FILE_NAME);
			fileReader = new FileReader(file);
			bufferReader = new BufferedReader(fileReader);
			LineNumberReader lineNum = new LineNumberReader(bufferReader);
			
			
			while((line=lineNum.readLine())!=null) {
				if(lineNum.getLineNumber()==((plNum+1)+3)) {
					String[] temp = line.split("\t");
					this.email = temp[0];
					break;
				}
			}
			
		}catch(FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		}catch (IOException e) {
			System.err.println("파일 읽기 에러");
		}
	}

	
	
	/* Player 보유패 읽어오는 함수 */
	public ArrayList<Card> readPlCard(int readNum) {
		// readNum ==> 사용자 순서
		int pattern = -1;
		int cNumber = -1;
		
		try{
			File file = new File(FILE_NAME);
			fileReader = new FileReader(FILE_NAME);
			bufferReader = new BufferedReader(fileReader);
			
			while((line=bufferReader.readLine())!=null){
				if(line.contains("CardOfPlayer")) {
					//CardOfPlayer을 포함하는 문자열의 마지막 문자 가져오기
					String Player = line.substring(0);
					char num = Player.charAt(Player.length()-1);
					String getPlNum = Character.toString(num);
					  //player의 숫자 
					if(Integer.parseInt(getPlNum)==readNum) {
						while((line=bufferReader.readLine())!=null) {
							if(line.contains("/")) {
								break;
							}
							String[] temp = line.split(" "); // 파일에는 문양과 숫자가 spacebar로 구분되어 있다고 가정
							pattern = Integer.parseInt(temp[0]);
							cNumber = Integer.parseInt(temp[1]);
							
							Card card = new Card(pattern, cNumber, 1);
							this.pCard.add(card);
						}
					
					}
				
				}
			}
			
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			System.err.println("파일 읽기 에러");
		}
		finally {
			if(fileReader!=null) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return this.pCard;
		
	}
	
	
	
	/* 사용자별 패 파일에 출력 (변경된 내용 반영(?)) ==> 변경된 CardOfPlayer(n) 내용 출력 */
	//this.pCard 에 있는 내용 index접근해서 한줄 씩 출력
	//CardOfPlayer(n) 전까지의 내용 dummy에 저장
	//CardOfPlayer(n+1)부터의 내용 dummy에 저장
	//CardOfPlayer(n) 전까지의 내용 덮어쓰기
	//CardOfPlayer(n)의 변경된 내용 출력 (덮어쓰기?)
	//CardOfPlyaer(n+1)부터의 내용 이어쓰기
	public void outPlCard(int plNum) {
		String dummy1 = "";
		String dummy2 = "";
		String dummy3 = "";
		String target = "CardOfPlayer"+plNum;
		
		try{
			File file = new File(FILE_NAME);
			fileReader = new FileReader(FILE_NAME);
			bufferReader = new BufferedReader(fileReader);
			
			if(file.length() != 0) {
				//CardOfPlayer(n) 전까지의 내용 저장 ==> dummy1
				
				while((line=bufferReader.readLine())!=null) {
					if(line.contains(target)) {
						break;
					}
					dummy1 += line+"\n";
				}
				
				//CardOfPlayer(n) 의 내용 저장 (CardOfPlayer(n)부터 구분자"/"까지) ==> dummy2
				while((line=bufferReader.readLine())!=null) {
					if(line.contains("/")) {
						break;
					}
					dummy2 += line+"\n";
				}
				//dummy2 += bufferReader.readLine(); // 마지막에 구분자 "/" 까지 dummy2 에 추가
				
				//CardOfPlayer(n+1) 부터의 내용 저장
				while((line=bufferReader.readLine())!=null) {
					dummy3 += line+"\n";
				}
			}else {
				System.err.println("파일에 기록된 내용이 없습니다.");
			}
			
			bufferReader.close();
			
			// 앞 내용 덮어쓰기 
			FileWriter fWriter=new FileWriter(file);
			fWriter.write(dummy1);
			fWriter.flush();
			fWriter.close();
			// 변경된 내용부터 이어쓰기 
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
			PrintWriter pw= new PrintWriter(bw,true);
			
			// 변경된 CardOfPlayer(n) 내용 출력 
			pw.write(target+"\n");
			//player(n) 의 pCard 내용 접근 후 출력
			
			for(int i=0; i<this.pCard.size(); i++) {
				Card card = pCard.get(i);
				int p = card.pattern();
				int n = card.number();
				pw.write(p+" "+n+"\n");
			}
			pw.write("/"+"\n"); //구분자 출력
			// 뒷 내용 (dummy3, CardOfPlayer(n+1) 부터의 내용)
			pw.write(dummy3);
			pw.flush();
			pw.close();
			
			
		} catch (FileNotFoundException e) {
			System.err.println("파일을 열 수 없습니다.");
		} catch (IOException e) {
			System.err.println("파일 읽기 에러");
		}
		finally {
			if(fileReader!=null) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
